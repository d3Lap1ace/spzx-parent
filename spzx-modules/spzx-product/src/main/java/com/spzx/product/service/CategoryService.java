package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.Category;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 15:22 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Service
public interface CategoryService extends IService<Category> {
    /**
     * 获取分类下拉树列表
     * @param id
     * @return
     */
    public List<Category> treeSelect(Long id);

    /**
     * 获取本层id以及上层id
     * @param categoryId
     * @return
     */
    List<Long> getCategoryByCategoryId(Long categoryId);
}
