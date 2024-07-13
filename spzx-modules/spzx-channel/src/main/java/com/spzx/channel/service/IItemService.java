package com.spzx.channel.service;

import com.spzx.channel.domain.ItemVo;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 13/7/2024 21:08 周六
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
public interface IItemService {
    ItemVo item(Long skuId);
}
