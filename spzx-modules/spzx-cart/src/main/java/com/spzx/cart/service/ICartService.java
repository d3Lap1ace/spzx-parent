package com.spzx.cart.service;

import com.spzx.cart.api.domain.CartInfo;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 19/7/2024 09:26 周五
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
public interface ICartService {
    void addToCart(Long skuId, Integer skuNum);

    List<CartInfo> getCartList();

    void checkCart(Long skuId, Integer isChecked);

    void allCheckCart(Integer isChecked);

    void clearCart();

    List<CartInfo> getCartCheckedList(Long userId);

    Boolean updateCartPrice(Long userId);

    Boolean deleteCartCheckedList(Long userId);

    void deleteCart(Long skuId);
}
