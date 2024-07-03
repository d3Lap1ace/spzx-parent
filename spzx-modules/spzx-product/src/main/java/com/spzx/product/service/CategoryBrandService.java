package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.CategoryBrand;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 15:57 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
public interface CategoryBrandService extends IService<CategoryBrand> {
    CategoryBrand selectCategoryBrandById(Long id);
}
