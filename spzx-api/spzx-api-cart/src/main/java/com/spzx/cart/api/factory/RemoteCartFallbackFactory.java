package com.spzx.cart.api.factory;

import com.spzx.cart.api.RemoteCartService;
import com.spzx.cart.api.domain.CartInfo;
import com.spzx.common.core.domain.R;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 19/7/2024 19:00 周五
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Component
public class RemoteCartFallbackFactory implements FallbackFactory<RemoteCartService> {
    @Override
    public RemoteCartService create(Throwable cause) {
        return new RemoteCartService() {
            @Override
            public R<List<CartInfo>> getCartCheckedList(Long userId, String source) {
                return R.fail("获取用户购物车选中数据失败:" + cause.getMessage());
            }

            @Override
            public R<Boolean> updateCartPrice(Long userId, String source) {
                return R.fail("更新购物车价格失败:" + cause.getMessage());
            }

            @Override
            public R<Boolean> deleteCartCheckedList(Long userId, String source) {
                return R.fail("删除用户购物车选中数据失败:" + cause.getMessage());
            }
        };
    }
}
