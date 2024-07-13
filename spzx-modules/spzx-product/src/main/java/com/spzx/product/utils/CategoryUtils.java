package com.spzx.product.utils;

import com.spzx.product.api.domain.vo.CategoryVo;


import java.util.ArrayList;
import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 13/7/2024 18:08 周六
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */


public class CategoryUtils {
    /**
     * 使用递归方法建分类树
     * @param categoryVoList
     * @return
     */
    public static List<CategoryVo> buildTree(List<CategoryVo> categoryVoList) {
        ArrayList<CategoryVo> tree = new ArrayList<>();
        categoryVoList.forEach(categoryVo -> {
            if (categoryVo.getParentId() == 0) {
                tree.add(findChildren(categoryVo,categoryVoList));
            }
        });
        return tree;
    }

    /**
     * 递归查找子节点
     * @param
     * @return
     */
    public static CategoryVo findChildren(CategoryVo categoryVo, List<CategoryVo> categoryVoList) {
        categoryVo.setChildren(new ArrayList<>());
        for (CategoryVo child : categoryVoList) {
            if (child.getParentId() == categoryVo.getId()){
                if(categoryVo.getChildren()==null){
                    categoryVo.setChildren(new ArrayList<>());
                }
                categoryVo.getChildren().add(findChildren(child,categoryVoList));
            }
        }
        return categoryVo;
    }





}
