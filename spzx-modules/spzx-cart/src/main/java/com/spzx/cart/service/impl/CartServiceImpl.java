package com.spzx.cart.service.impl;

import com.spzx.cart.api.domain.CartInfo;
import com.spzx.cart.service.ICartService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.SkuPrice;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 19/7/2024 09:27 周五
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
@Slf4j
@Service
public class CartServiceImpl implements ICartService {

    @Resource
    private RemoteProductService remoteProductService;
    @Autowired
    private RedisTemplate redisTemplate;

    //定义key user:userId:cart
    private String getCartKey(Long userId) {
        return "user:cart" + userId;
    }


    /**
     * 添加购物车
     * @param skuId
     * @param skuNum
     */
    @Override
    public void addToCart(Long skuId, Integer skuNum) {
        log.info("Add to cart");
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        String userName = SecurityContextHolder.getUserName();
        //1.构建“用户”购物车hash结构key  user:cart+userId
        String cartKey = this.getCartKey(userId);

        //2.创建Hash结构绑定操作对象（方便对hash进行操作）
//        BoundHashOperations<String, String, String> hashOps =redisTemplate.boundHashOps(cartKey);
        BoundHashOperations<String,String,CartInfo> hashOps = redisTemplate.boundHashOps(cartKey);

        //4.判断用户购物车中是否包含该商品 如果包含：数量进行累加(某件商品数量上限99) 不包含：新增购物车商品
        String hashkey = skuId.toString();
        Integer threshold = 99;
        Boolean flag = hashOps.hasKey(hashkey);
        if(flag) {
            //4.1 说明该商品在购物车中已有，对数量进行累加 ，不能超过指定上限99
//            String str = hashOps.get(hashKey);
//            CartInfo cartInfo = JSON.parseObject(str,CartInfo.class);
            CartInfo cartInfo = hashOps.get(hashkey);
            cartInfo.setSkuNum(skuNum > threshold ? threshold : skuNum);
            hashOps.put(hashkey, cartInfo);
//            hashOps.put(hashKey, JSON.toJSONString(cartInfo));
        }else {
            //4.2.判断购物车商品种类（不同SKU）总数大于50件
            Long count = hashOps.size();
            if(++count>50){
                throw new RuntimeException("商品种类数量超过上限！");
            }
            //4.3. 说明购物车没有该商品，构建购物车对象，存入Redis
            CartInfo cartInfo = new CartInfo();
            cartInfo.setUserId(userId);
            cartInfo.setSkuId(skuId);
            cartInfo.setSkuNum(skuNum);
            cartInfo.setCreateTime(new Date());
            cartInfo.setCreateBy(userName);
            cartInfo.setUpdateTime(new Date());
            cartInfo.setUpdateBy(userName);
            //4.3.1 远程调用商品服务获取商品sku基本信息
            R<ProductSku> productSkuR = remoteProductService.getProductSku(skuId, SecurityConstants.INNER);
            if(R.FAIL == productSkuR.getCode()) {
                throw new ServiceException(productSkuR.getMsg());
            }
            ProductSku productSku = productSkuR.getData();
            cartInfo.setSkuName(productSku.getSkuName());
            cartInfo.setThumbImg(productSku.getThumbImg());
            //4.2 远程调用商品服务获取商品实时价格
            R<SkuPrice> skuPriceR = remoteProductService.getSkuPrice(skuId, SecurityConstants.INNER);
            if(R.FAIL == skuPriceR.getCode()) {
                throw new ServiceException(skuPriceR.getMsg());
            }
            SkuPrice skuPrice = skuPriceR.getData();
            cartInfo.setCartPrice(skuPrice.getSalePrice());
            cartInfo.setSkuPrice(skuPrice.getSalePrice());
            //4.3 将购物车商品存入Redis
            hashOps.put(hashkey, cartInfo);
//            hashOps.put(hashKey, JSON.toJSONString(cartInfo));
        }
    }

    @Override
    public List<CartInfo> getCartList() {
        // 获取当前用户id
        Long userId = SecurityContextHolder.getUserId();
        // 从redis中获取用户hash  生成key
        String cartKey = this.getCartKey(userId);
        // 根据key获取购物车value值
        List<CartInfo> cartInfoList = redisTemplate.opsForHash().values(cartKey);
        // 处理集合数据
        Optional.of(cartInfoList)
                .ifPresent(cartInfoListNotNull->{
                    // 按添加时间顺序排队
                    List<CartInfo> infoList = cartInfoListNotNull
                            .stream()
                            .sorted(((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())))
                            .collect(Collectors.toList());
                    // infoList获取购物车里面所有skuId
                    List<Long> skuIdList = infoList.stream().map(CartInfo::getSkuId).collect(Collectors.toList());

                    //根据所有skuid列表远程调用，对应sku价格数据
                    R<List<SkuPrice>> skuPriceListR = remoteProductService.getSkuPriceList(skuIdList, SecurityConstants.INNER);
                    if(R.FAIL == skuPriceListR.getCode()) {
                        throw new ServiceException(skuPriceListR.getMsg());
                    }
                    List<SkuPrice> skuPriceList = skuPriceListR.getData();

                    //skuPriceList 转换map集合  key：skuId   value：价格
                    Map<Long, BigDecimal> map = skuPriceList.stream().collect(Collectors.toMap(SkuPrice::getSkuId, SkuPrice::getSalePrice));

                    //infoList购物车所有商品价格更新最新价格
                    infoList.forEach(cartInfo -> {
                        Long skuId = cartInfo.getSkuId();
                        BigDecimal price = map.get(skuId);
                        cartInfo.setSkuPrice(price);
                    });
                });
        return cartInfoList;
    }

    @Override
    public void checkCart(Long skuId, Integer isChecked) {
        // 获取当前用户id
        Long userId = SecurityContextHolder.getUserId();
        // 修改缓存
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        // 先获取用户选择的商品
        if(hashOperations.hasKey(skuId.toString())){
            CartInfo cartInfoUpd = hashOperations.get(skuId.toString());

            cartInfoUpd.setIsChecked(isChecked);
            hashOperations.put(skuId.toString(), cartInfoUpd);
        }

    }

    @Override
    public void allCheckCart(Integer isChecked) {
        // 获取当前用户id
        Long userId = SecurityContextHolder.getUserId();
        // 修改缓存
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = hashOperations.values();
        cartInfoList.forEach(it->{
            CartInfo cartInfoUpd = hashOperations.get(it.getSkuId().toString());
            cartInfoUpd.setIsChecked(isChecked);

            // 更新缓存
            hashOperations.put(it.getSkuId().toString(), cartInfoUpd);
        });

    }

    @Override
    public void clearCart() {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        //获取缓存对象
        redisTemplate.delete(cartKey);
    }

    /**
     * 查询用户购物车列表中选中商品列表
     * @param userId
     * @return
     */
    @Override
    public List<CartInfo> getCartCheckedList(Long userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        // 优雅
        List<CartInfo> cartCachInfoList = redisTemplate.opsForHash().values(this.getCartKey(userId));
        Optional.ofNullable(cartCachInfoList)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(cartInfo -> cartInfo.getIsChecked().intValue()==1)
                .forEach(cartInfoList::add);
        return cartInfoList;
    }

    @Override
    public Boolean updateCartPrice(Long userId) {

        String cartKey = this.getCartKey(userId); // 获取当前用户id
        BoundHashOperations<String,String,CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartCachInfoList = hashOperations.values(); // 获取redis里面 当前用户购物车的物品

//        Optional.ofNullable(cartCachInfoList)
//                .ifPresent(cartInfoList -> {cartInfoList
//                            .stream()
//                            .filter(cartInfo -> cartInfo.getIsChecked().intValue()==1)
//                            .forEach(cartInfo -> {
//                                SkuPrice skuPrice = remoteProductService.getSkuPrice(cartInfo.getSkuId(), SecurityConstants.INNER).getData();
//                                cartInfo.setCartPrice(skuPrice.getSalePrice());
//                                cartInfo.setSkuPrice(skuPrice.getSalePrice());
//                                hashOperations.put(cartInfo.getSkuId().toString(), cartInfo);
//                            });
//                });
        if (!CollectionUtils.isEmpty(cartCachInfoList)) {
            for (CartInfo cartInfo : cartCachInfoList) {
                if (cartInfo.getIsChecked().intValue() == 1) {
                    SkuPrice skuPrice = remoteProductService.getSkuPrice(cartInfo.getSkuId(), SecurityConstants.INNER).getData();
                    cartInfo.setCartPrice(skuPrice.getSalePrice());
                    cartInfo.setSkuPrice(skuPrice.getSalePrice());
                    hashOperations.put(cartInfo.getSkuId().toString(), cartInfo);
                }
            }
        }

        return true;
    }

    @Override
    public Boolean deleteCartCheckedList(Long userId) {
        String cartKey = getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartCachInfoList = hashOperations.values();
        Optional.ofNullable(cartCachInfoList)
                .orElseGet(ArrayList::new)
                .stream()
                .forEach(cartInfo -> {
                    if(cartInfo.getIsChecked().intValue()==1){
                        hashOperations.delete(cartInfo.getSkuId().toString());
                    }
                });
        return true;
    }

    @Override
    public void deleteCart(Long skuId) {
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        boundHashOperations.delete(skuId.toString());
    }
}
