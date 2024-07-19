package com.spzx.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.cart.api.RemoteCartService;
import com.spzx.cart.api.domain.CartInfo;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.order.domain.OrderForm;
import com.spzx.order.domain.OrderInfo;
import com.spzx.order.domain.OrderItem;
import com.spzx.order.domain.TradeVo;
import com.spzx.order.mapper.OrderInfoMapper;
import com.spzx.order.service.IOrderInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单Service业务层处理
 *
 * @author atguigu
 * @date 2024-07-10
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService
{
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private RemoteCartService remoteCartService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询订单列表
     *
     * @param orderInfo 订单
     * @return 订单
     */
    @Override
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo)
    {
        return orderInfoMapper.selectOrderInfoList(orderInfo);
    }

    @Override
    public TradeVo orderTradeData() {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        R<List<CartInfo>> cartInfoListResult = remoteCartService.getCartCheckedList(userId, SecurityConstants.INNER);
        if(R.FAIL==cartInfoListResult.getCode()){
            throw new ServiceException(cartInfoListResult.getMsg());
        }
        List<CartInfo> cartInfoList = cartInfoListResult.getData();
        if(CollectionUtils.isEmpty(cartInfoList)){
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
        orderItemList.forEach(orderItem -> {
            totalAmount.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));
        });

        //渲染订单确认页面-生成用户流水号
        String tradeNo = this.generateTradeNo(userId);
        TradeVo tradeVo = new TradeVo();
        tradeVo.setTradeNo(tradeNo);
        tradeVo.setTotalAmount(totalAmount);
        tradeVo.setOrderItemList(orderItemList);
        return tradeVo;
    }

    @Override
    public Long submitOrder(OrderForm orderForm) {
        return 0L;
    }

    private String generateTradeNo(Long userId) {
        //1.构建流水号Key
        String userTradeKey = "user:tradeNo:" + userId;

        //2.构建流水号value
        String tradeNo = UUID.randomUUID().toString().replaceAll("-", "");

        //3.将流水号存入Redis 暂存5分钟
        redisTemplate.opsForValue().set(userTradeKey,tradeNo,5, TimeUnit.MINUTES);

        return  tradeNo;

    }

}
