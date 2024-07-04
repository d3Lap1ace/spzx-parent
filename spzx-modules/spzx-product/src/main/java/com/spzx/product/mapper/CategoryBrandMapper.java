package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.domain.Brand;
import com.spzx.product.domain.CategoryBrand;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 16:00 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
public interface CategoryBrandMapper extends BaseMapper<CategoryBrand> {

    /**
     * 查询分类品牌列表
     *
     * @param categoryBrand 分类品牌
     * @return 分类品牌集合
     */
    List<CategoryBrand> selectCategoryBrandList(CategoryBrand categoryBrand);

    List<Brand> getBrandListByCategoryId(Long categoryId);
}
