package com.spzx.product.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.CategoryBrand;
import com.spzx.product.mapper.CategoryBrandMapper;
import com.spzx.product.service.CategoryBrandService;
import com.spzx.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 15:58 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
@Service
public class CategoryBrandServiceImpl extends ServiceImpl<CategoryBrandMapper, CategoryBrand> implements CategoryBrandService{

    @Autowired
    private CategoryService categoryService;


    /**
     * 分类品牌详细
     * @param id
     * @return
     */

    @Override
    public CategoryBrand selectCategoryBrandById(Long id) {
        CategoryBrand categoryBrand = baseMapper.selectById(id); // 根据id查询分配品牌
        List<Long> categoryIdList = categoryService.getCategoryByCategoryId(categoryBrand.getCategoryId());
        categoryBrand.setCategoryIdList(categoryIdList);
        return categoryBrand;
    }
}
