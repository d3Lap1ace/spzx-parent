package com.spzx.channel.service;

import com.spzx.product.api.domain.vo.CategoryVo;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 13/7/2024 14:26 周六
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
public interface ICategoryService {
    List<CategoryVo> tree();
}
