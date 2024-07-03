package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.CategoryBrand;
import com.spzx.product.service.CategoryBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

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

    /**
     * 查询分类品牌列表
     * @param categoryBrand
     * @return
     */
    @Operation(summary = "查询分类品牌列表")
    @GetMapping("/list")
    public TableDataInfo getlist(CategoryBrand categoryBrand){
        startPage();
        List<CategoryBrand> list = categoryBrandService.selectCategoryBrandList(categoryBrand);
        return getDataTable(list);
    }


    /**
     * 获取分类品牌详细信息
     * @param id
     * @return
     */
    @Operation(summary = "获取分类品牌详细信息")
    @GetMapping( "/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(categoryBrandService.selectCategoryBrandById(id));
    }

    /**
     * 新增分类品牌
     * @param categoryBrand
     * @return
     */
    @Operation(summary = "新增分类品牌")
    @PostMapping
    public AjaxResult insertCategoryBrand(@RequestBody CategoryBrand categoryBrand){
        categoryBrand.setCreateBy(SecurityUtils.getUsername());
        return toAjax(categoryBrandService.insertCategoryBrand(categoryBrand));
    }


    @Operation(summary = "修改分类品牌")
    @PutMapping
    public AjaxResult updateCategoryBrand(@RequestBody CategoryBrand categoryBrand){
        categoryBrand.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(categoryBrandService.updateCategoryBrand(categoryBrand));
    }

    @Operation(summary = "删除分类品牌")
    @DeleteMapping("/{ids}")
    public AjaxResult deleteCategoryBrandById(@PathVariable Long[] ids){
        return toAjax(categoryBrandService.removeBatchByIds(Arrays.asList(ids)));
    }
}
