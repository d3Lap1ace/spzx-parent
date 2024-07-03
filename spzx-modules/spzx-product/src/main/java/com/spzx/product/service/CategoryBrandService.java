package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.CategoryBrand;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 15:57 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
public interface CategoryBrandService extends IService<CategoryBrand> {

    /**
     * 查询分类品牌列表
     * @param categoryBrand
     * @return
     */
    List<CategoryBrand> selectCategoryBrandList(CategoryBrand categoryBrand);

    /**
     * 获取分类品牌详细信息
     * @param id
     * @return
     */
    CategoryBrand selectCategoryBrandById(Long id);

    /**
     * 新增分类品牌
     * @param categoryBrand
     * @return
     */
    int insertCategoryBrand(CategoryBrand categoryBrand);

    /**
     * 修改分类品牌
     * @param categoryBrand
     * @return
     */
    int updateCategoryBrand(CategoryBrand categoryBrand);
}
