package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.CategoryBrand;
import com.spzx.product.service.CategoryBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 15:57 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Tag(name = "品牌分类接口管理")
@RestController
@RequestMapping("/categoryBrand")
public class CategoryBrandController extends BaseController {
    @Autowired
    private CategoryBrandService categoryBrandService;

    @Operation(summary = "查询分类品牌列表")
    @GetMapping("/list")
    public TableDataInfo getlist(CategoryBrand categoryBrand){
        return null;
    }



    /**
     * 获取分类品牌详细信息
     */
    @Operation(summary = "获取分类品牌详细信息")
    @GetMapping( "/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(categoryBrandService.selectCategoryBrandById(id));
    }

}
