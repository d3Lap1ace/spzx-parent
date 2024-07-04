package com.spzx.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.domain.Brand;
import com.spzx.product.domain.CategoryBrand;
import com.spzx.product.mapper.CategoryBrandMapper;
import com.spzx.product.service.ICategoryBrandService;
import com.spzx.product.service.ICategoryService;
import jakarta.annotation.Resource;
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
public class CategoryBrandServiceImpl extends ServiceImpl<CategoryBrandMapper, CategoryBrand> implements ICategoryBrandService{

    @Resource
    private ICategoryService categoryService;
    @Resource
    private CategoryBrandMapper categoryBrandMapper;


    @Override
    public CategoryBrand selectCategoryBrandById(Long id) {
        CategoryBrand categoryBrand = baseMapper.selectById(id); // 根据id查询分配品牌
        List<Long> categoryIdList = categoryService.getCategoryByCategoryId(categoryBrand.getCategoryId());
        categoryBrand.setCategoryIdList(categoryIdList);
        return categoryBrand;
    }

    @Override
    public List<CategoryBrand> selectCategoryBrandList(CategoryBrand categoryBrand) {
        return baseMapper.selectCategoryBrandList(categoryBrand);
    }


    @Override
    public int insertCategoryBrand(CategoryBrand categoryBrand) {
        // 查询是否已经在分类添加过该品牌
        LambdaQueryWrapper<CategoryBrand> categoryBrandLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryBrandLambdaQueryWrapper.eq(CategoryBrand::getCategoryId,categoryBrand.getCategoryId())
                        .eq(CategoryBrand::getBrandId,categoryBrand.getBrandId());
        Long count = baseMapper.selectCount(categoryBrandLambdaQueryWrapper);
        if(count > 0){
            throw new ServiceException("该分类已加载该品牌");
        }
        return baseMapper.insert(categoryBrand);
    }

    @Override
    public int updateCategoryBrand(CategoryBrand categoryBrand) {

        CategoryBrand brand = this.getById(categoryBrand.getId());
        if(categoryBrand.getCategoryId().longValue() != brand.getCategoryId().longValue()
                || categoryBrand.getBrandId().longValue() != brand.getBrandId().longValue()){
            LambdaQueryWrapper<CategoryBrand> categoryBrandLambdaQueryWrapper = new LambdaQueryWrapper<>();
            categoryBrandLambdaQueryWrapper.eq(CategoryBrand::getCategoryId,categoryBrand.getCategoryId())
                    .eq(CategoryBrand::getBrandId,categoryBrand.getBrandId());
            Long count = baseMapper.selectCount(categoryBrandLambdaQueryWrapper);
            if(count > 0){
                throw new ServiceException("该分类已加载该品牌");
            }
        }
        return baseMapper.updateById(categoryBrand);
    }

    @Override
    public List<Brand> getBrandListByCategoryId(Long categoryId) {
        return categoryBrandMapper.getBrandListByCategoryId(categoryId);
    }

}
