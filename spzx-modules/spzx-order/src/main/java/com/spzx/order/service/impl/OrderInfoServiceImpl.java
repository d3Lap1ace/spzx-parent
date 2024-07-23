package com.spzx.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.cart.api.RemoteCartService;
import com.spzx.cart.api.domain.CartInfo;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.common.rabbit.service.RabbitService;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.order.api.domain.OrderInfo;
import com.spzx.order.api.domain.OrderItem;
import com.spzx.order.domain.OrderForm;
import com.spzx.order.domain.OrderLog;
import com.spzx.order.domain.TradeVo;
import com.spzx.order.mapper.OrderInfoMapper;
import com.spzx.order.mapper.OrderItemMapper;
import com.spzx.order.mapper.OrderLogMapper;
import com.spzx.order.service.IOrderInfoService;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.SkuPrice;
import com.spzx.user.api.RemoteUserAddressService;
import com.spzx.user.api.domain.UserAddress;
import jakarta.annotation.Resource;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单Service业务层处理
 *
 * @author atguigu
 * @date 2024-07-10
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService {

    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private RemoteCartService remoteCartService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Resource
    private RemoteProductService remoteProductService;
    @Resource
    private RemoteUserAddressService remoteUserAddressService;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Resource
    private OrderLogMapper orderLogMapper;
    @Autowired
    private RabbitService rabbitService;


    /**
     * 查询订单列表
     *
     * @param orderInfo 订单
     * @return 订单
     */
    @Override
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo) {
        return orderInfoMapper.selectOrderInfoList(orderInfo);
    }

    @Override
    public TradeVo orderTradeData() {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        R<List<CartInfo>> cartInfoListResult = remoteCartService.getCartCheckedList(userId, SecurityConstants.INNER);
        if (R.FAIL == cartInfoListResult.getCode()) {
            throw new ServiceException(cartInfoListResult.getMsg());
        }
        List<CartInfo> cartInfoList = cartInfoListResult.getData();
        if (CollectionUtils.isEmpty(cartInfoList)) {
            throw new ServiceException("购物车无选中商品");
        }


        //将集合泛型从购物车改为订单明细
        BigDecimal totalAmount = new BigDecimal(0);
        List<OrderItem> orderItemList = Optional.ofNullable(cartInfoList)
                .orElseGet(ArrayList::new)
                .stream()
                .map(cartInfo -> {
                    OrderItem orderItem = new OrderItem();
                    BeanUtils.copyProperties(cartInfo, orderItem, OrderItem.class);
                    orderItem.setSkuPrice(cartInfo.getSkuPrice());
                    return orderItem;
                }).collect(Collectors.toList());
//                .stream().forEach(orderItem -> {
//                    totalAmount.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));
//                });
        //订单总金额
        //订单总金额
        for (OrderItem orderItem : orderItemList) {
            totalAmount = totalAmount.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));
        }

        //渲染订单确认页面-生成用户流水号
        String tradeNo = this.generateTradeNo(userId);
        TradeVo tradeVo = new TradeVo();
        tradeVo.setTradeNo(tradeNo);
        tradeVo.setTotalAmount(totalAmount);
        tradeVo.setOrderItemList(orderItemList);
        return tradeVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long submitOrder(OrderForm orderForm) {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();

        //  保证订单流水号具备原子性，使用lua脚本
        String userTradeKey = "user:tradeNo:" + userId;
        String scriptText = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                "then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(scriptText);
        redisScript.setResultType(Long.class);
        Long flag = (Long) redisTemplate.execute(redisScript, Arrays.asList(userTradeKey), orderForm.getTradeNo());
        if (flag == 0) {
            throw new ServiceException("请勿重复提交订单，请尝试重试");
        }

        // 2. 判断购物项
        List<OrderItem> orderItemList = orderForm.getOrderItemList();
        if (CollectionUtils.isEmpty(orderItemList)) {
            throw new ServiceException("请求不合法");
        }

        // 3.订单校验
        // 3.1.获得商品id列表
        List<Long> skuIdList = orderItemList.stream().map(OrderItem::getSkuId).collect(Collectors.toList());
        // 3.2 根据商品id列表 远程调用 获得商品价格列表
        R<List<SkuPrice>> skuPriceListResult = remoteProductService.getSkuPriceList(skuIdList, SecurityConstants.INNER);
        if (R.FAIL == skuPriceListResult.getCode()) {
            throw new ServiceException(skuPriceListResult.getMsg());
        }
        List<SkuPrice> skuPriceList = skuPriceListResult.getData();

        // 3.3 把查询sku价格列表转为map集合
        Map<Long, BigDecimal> skuIdToSalePriceMap = skuPriceList.stream().
                collect(Collectors.toMap(SkuPrice::getSkuId, SkuPrice::getSalePrice));

        //3.4 比较orderItemList价格和最新价格是否相同，如果不相同，价格变化
        String priceCheckResult = "";

        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getSkuPrice().compareTo(skuIdToSalePriceMap.get(orderItem.getSkuId())) != 0) {
                priceCheckResult += orderItem.getSkuName() + "价格变化了; ";
            }
        }

        //3.5 如果价格变化，远程调用更新购物车商品价格
        if (StringUtils.hasText(priceCheckResult)) {
            remoteCartService.updateCartPrice(userId, SecurityConstants.INNER);
            throw new ServiceException(priceCheckResult);
        }


        try {
            // 4 校验库存并锁定库存
            Long orderId = this.saveOrder(orderForm);


            //5 删除购物车选中商品
            remoteCartService.deleteCartCheckedList(userId, SecurityConstants.INNER);

            //6 发送延迟消息，实现30分钟没有支付订单，订单关闭
            this.sendDelayMessage(orderId);

            return orderId;
        } catch (Exception e) {
            rabbitService.sendMessage(MqConst.EXCHANGE_PRODUCT, MqConst.QUEUE_UNLOCK, orderForm.getTradeNo());
            throw new ServiceException("下单失败");
        }
    }

    private void sendDelayMessage(Long orderId) {
        // 1. Redisson创建阻塞队列
        RBlockingDeque<Object> blockingDeque = redissonClient.getBlockingDeque(MqConst.ROUTING_CANCEL_ORDER);
        // 2. 在阻塞队列基础上，创建延迟队列
        RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);

        // 3. 向延迟队列放消息
        delayedQueue.offer(orderId.toString(), 10, TimeUnit.SECONDS);
    }


    @Override
    public TradeVo buy(Long skuId) {
        Long userId = SecurityUtils.getUserId();
        ProductSku productSku = remoteProductService.getProductSku(skuId, SecurityConstants.INNER).getData();
        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setSkuId(skuId);
        orderItem.setSkuName(productSku.getSkuName());
        orderItem.setSkuNum(1);
        orderItem.setSkuPrice(productSku.getSalePrice());
        orderItem.setThumbImg(productSku.getThumbImg());
        orderItemList.add(orderItem);

        //订单总金额
        BigDecimal totalAmount = productSku.getSalePrice();

        //渲染订单确认页面-生成用户流水号
        String tradeNo = this.generateTradeNo(SecurityUtils.getUserId());


        TradeVo tradeVo = new TradeVo();
        tradeVo.setTotalAmount(totalAmount);
        tradeVo.setOrderItemList(orderItemList);
        tradeVo.setTradeNo(tradeNo);
        return tradeVo;


    }

    @Override
    public List<OrderInfo> selectUserOrderInfoList(Integer orderStatus) {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        // 根据订单状态条件值是否为空, 如果为空查询全部
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getUserId, userId);
        if (orderStatus != null) {
            wrapper.eq(OrderInfo::getOrderStatus, orderStatus);
        }
        List<OrderInfo> orderInfoList = baseMapper.selectList(wrapper);

        // 把每个订单里面所有订单项查询,进行封装
        List<Long> orderIdList = Optional.ofNullable(orderInfoList)
                .orElseThrow()
                .stream()
                .map(OrderInfo::getId)
                .collect(Collectors.toList());
        Map<Long, List<OrderItem>> orderIdToOrderItemListMap = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                        .in(OrderItem::getOrderId, orderIdList))
                .stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));
        orderInfoList.forEach(it -> {
            it.setOrderItemList(orderIdToOrderItemListMap.get(it.getId()));
        });

        return orderInfoList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processCloseOrder(long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if (orderInfo == null && orderInfo.getOrderStatus().intValue() == 0) {
            orderInfo.setOrderStatus(-1);
            orderInfo.setCancelTime(new Date());
            orderInfo.setCancelReason("自动取消");
            orderInfoMapper.updateById(orderInfo);

            //记录日志
            OrderLog orderLog = new OrderLog();
            orderLog.setNote("系统取消");
            orderLog.setOrderId(orderInfo.getId());
            orderLog.setProcessStatus(-1L);
            orderLog.setOperateUser("后台管理");
            orderLogMapper.insert(orderLog);

            //发送MQ消息通知商品系统解锁库存
            rabbitService.sendMessage(MqConst.EXCHANGE_PRODUCT, MqConst.QUEUE_UNLOCK, orderInfo.getOrderNo());

        }
    }

    @Override
    public void cancelOrder(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if (null != orderInfo && orderInfo.getOrderStatus().intValue() == 0) {
            orderInfo.setOrderStatus(-1);
            orderInfo.setCancelTime(new Date());
            orderInfo.setCancelReason("用户取消订单");
            orderInfoMapper.updateById(orderInfo);
            //记录日志
            OrderLog orderLog = new OrderLog();
            orderLog.setOrderId(orderInfo.getId());
            orderLog.setProcessStatus(-1l);
            orderLog.setNote("用户取消订单");
            orderLogMapper.insert(orderLog);
            //发送MQ消息通知商品系统解锁库存
            rabbitService.sendMessage(MqConst.EXCHANGE_PRODUCT, MqConst.QUEUE_UNLOCK, orderInfo.getOrderNo());
        }
    }

    /**
     * 根据订单号获取订单信息
     * @param orderNo
     * @return
     */
    @Override
    public OrderInfo getByOrderNo(String orderNo) {
        OrderInfo orderInfo = orderInfoMapper.selectOne(new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getOrderNo, orderNo));
        List<OrderItem> orderItemList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderInfo.getId()));
        orderInfo.setOrderItemList(orderItemList);
        return orderInfo;
    }



    @Transactional(rollbackFor = Exception.class)
    public Long saveOrder(OrderForm orderForm) {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        String userName = SecurityContextHolder.getUserName();

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(orderForm.getTradeNo());
        orderInfo.setUserId(userId);
        orderInfo.setNickName(userName);
        orderInfo.setRemark(orderForm.getRemark());
        UserAddress userAddress = remoteUserAddressService.getUserAddress(orderForm.getUserAddressId(), SecurityConstants.INNER).getData();
        orderInfo.setReceiverName(userAddress.getName());
        orderInfo.setReceiverPhone(userAddress.getPhone());
        orderInfo.setReceiverTagName(userAddress.getTagName());
        orderInfo.setReceiverProvince(userAddress.getProvinceCode());
        orderInfo.setReceiverCity(userAddress.getCityCode());
        orderInfo.setReceiverDistrict(userAddress.getDistrictCode());
        orderInfo.setReceiverAddress(userAddress.getFullAddress());

        List<OrderItem> orderItemList = orderForm.getOrderItemList();
        BigDecimal totalAmount = new BigDecimal(0);
        orderItemList.forEach(orderItem -> {
            totalAmount.add(orderItem.getSkuPrice()).multiply(new BigDecimal(orderItem.getSkuNum()));
        });

        orderInfo.setTotalAmount(totalAmount);
        orderInfo.setCouponAmount(new BigDecimal(0));
        orderInfo.setOriginalTotalAmount(totalAmount);
        orderInfo.setFeightFee(orderForm.getFeightFee());
        orderInfo.setOrderStatus(0);
        orderInfo.setCreateBy(userName);
        orderInfo.setCreateTime(new Date());

        orderInfoMapper.insert(orderInfo);

        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderId(orderInfo.getId());
            orderItemMapper.insert(orderItem);
        }

        //返回订单id
        return orderInfo.getId();

    }

    private String generateTradeNo(Long userId) {
        //1.构建流水号Key
        String userTradeKey = "user:tradeNo:" + userId;

        //2.构建流水号value
        String tradeNo = UUID.randomUUID().toString().replaceAll("-", "");

        //3.将流水号存入Redis 暂存5分钟
        redisTemplate.opsForValue().set(userTradeKey, tradeNo, 5, TimeUnit.MINUTES);

        return tradeNo;

    }

}
