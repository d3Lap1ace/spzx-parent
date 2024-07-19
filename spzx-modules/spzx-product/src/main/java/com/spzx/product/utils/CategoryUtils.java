package com.spzx.product.utils;

import com.spzx.product.api.domain.vo.CategoryVo;
import java.util.ArrayList;
import java.util.List;


/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 13/7/2024 11:09 周六
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
    public static List<CategoryVo> buildTree (List<CategoryVo> categoryVoList){

        List<CategoryVo> trees = new ArrayList<>();
        categoryVoList.forEach(categoryVo -> {
            if(categoryVo.getParentId().longValue() == 0){
                trees.add(findChildren(categoryVo,categoryVoList));
            }
        });
        return trees;
    }

    // 目前方法里面 categoryVo代表第一层分类数据
    //            categoryVoList 所有分类数据
    //根据第一层，到categoryVoList所有分类集合找到第二层，递归方式继续往下找
    public static CategoryVo findChildren(CategoryVo categoryVo,List<CategoryVo> categoryVoList){
        //遍历所有分类集合 categoryVoList
        categoryVo.setChildren(new ArrayList<CategoryVo>());
        categoryVoList.forEach(cvo->{
            if(cvo.getParentId().longValue() == categoryVo.getId().longValue()) {
                if (categoryVo.getChildren() == null) {
                    categoryVo.setChildren(new ArrayList<>());
                }
                categoryVo.getChildren().add(findChildren(cvo,categoryVoList));
            }

        });
        return categoryVo;
    }

}
