package com.spzx.product.controller;

import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.product.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.spzx.common.core.web.domain.AjaxResult.success;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 15:20 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Tag(name = "商品分类接口管理")
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    @Operation(summary = "获取分类下拉树列表")
    @GetMapping("/treeSelect/{id}")
    public AjaxResult treeSelect(@PathVariable Long id){
        return success(categoryService.treeSelect(id));
    }
}
