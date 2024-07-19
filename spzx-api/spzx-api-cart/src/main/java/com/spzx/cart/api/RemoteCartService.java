package com.spzx.cart.api;

import com.spzx.cart.api.domain.CartInfo;
import com.spzx.cart.api.factory.RemoteCartFallbackFactory;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 19/7/2024 18:53 周五
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@FeignClient(value = ServiceNameConstants.CART_SERVICE,fallbackFactory = RemoteCartFallbackFactory.class)
public interface RemoteCartService {
    @GetMapping("/getCartCheckedList/{userId}")
    R<List<CartInfo>> getCartCheckedList(@PathVariable("userId") Long userId,
                                         @RequestHeader(SecurityConstants.FROM_SOURCE)String source);

    @GetMapping("/updateCartPrice/{userId}")
    public R<Boolean> updateCartPrice(@PathVariable("userId") Long userId,
                                      @RequestHeader(SecurityConstants.FROM_SOURCE)String source);

    @GetMapping("/deleteCartCheckedList/{userId}")
    public R<Boolean> deleteCartCheckedList(@PathVariable("userId") Long userId,
                                            @RequestHeader(SecurityConstants.FROM_SOURCE)String source);
}
