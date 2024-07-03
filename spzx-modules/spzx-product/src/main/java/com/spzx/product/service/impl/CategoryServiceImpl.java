package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.Category;
import com.spzx.product.mapper.CategoryMapper;
import com.spzx.product.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 15:23 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;
    @Override
    public List<Category> treeSelect(Long id) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId,id);
        List<Category> list = categoryMapper.selectList(wrapper);

        if(!CollectionUtils.isEmpty(list)){
            list.forEach(it->{
                long count = categoryMapper.selectCount(new LambdaQueryWrapper<Category>()
                        .eq(Category::getParentId,it.getId()));
                if(count > 0){
                    it.setHasChildren(true);
                }else {
                    it.setHasChildren(false);
                }
            });
        }
        return list;
    }

    @Override
    public List<Long> getCategoryByCategoryId(Long categoryId) {
        ArrayList<Long> list = new ArrayList<>();

        List<Category> categoryList = getParentCategory(categoryId,new ArrayList<Category>());

        int size = categoryList.size();
        for(int i = size;i > 0; --i){
            Category category = categoryList.get(i - 1);
            Long id = category.getId();
            list.add(id);
        }
        return list;
    }

    private List<Category> getParentCategory(Long categoryId, ArrayList<Category> categoryList) {

        while (categoryId > 0){
            LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
            categoryLambdaQueryWrapper.eq(Category::getId,categoryId).select(Category::getId,Category::getParentId);
            Category category = categoryMapper.selectOne(categoryLambdaQueryWrapper);;
            categoryList.add(category);
            return getParentCategory(category.getParentId(),categoryList);
        }
        return categoryList;
    }
}
