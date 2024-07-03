package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.Brand;
import com.spzx.product.service.IBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 2/7/2024 18:08 周二
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Tag(name = "品牌管理接口")
@RestController
@RequestMapping("/brand")
public class BrandController extends BaseController {

    @Autowired
    private IBrandService brandService;

    /**
     * 获取品牌列表
     * @return
     */
    @Operation(summary = "查询品牌列表")
    @GetMapping("/list")
    public TableDataInfo list(Brand brand){
        startPage();
        List<Brand> list = brandService.selectBrandList(brand);
        return getDataTable(list);
    }


    /**
     * 获取品牌详细信息
     * @param id
     * @return
     */
    @Operation(summary = "获取品牌详细信息")
    @GetMapping("/{id}")
    public AjaxResult getBrandInfo(@PathVariable Long id){
        return success(brandService.getBrandInfo(id));
    }

    /**
     * 新增品牌信息
     * @param brand
     * @return
     */
    @Operation(summary = "新增品牌信息")
    @PostMapping
    public AjaxResult insertBrand(@RequestBody Brand brand){
        return success(brandService.insertBrand(brand));
    }

    /**
     * 修改信息
     * @param brand
     * @return
     */
    @Operation(summary = "修改信息")
    @PutMapping
    public AjaxResult updateBrand(@RequestBody Brand brand){
        brand.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(brandService.updateBrand(brand));

    }

    /**
     * 删除信息
     * @param
     * @return
     */
    @Operation(summary = "删除信息")
    @PostMapping("/{ids}")
    public AjaxResult deleteBrand(@PathVariable Long[] ids){
        return toAjax(brandService.deleteBrandById(ids));
    }

    /**
     * 获取全部品牌
     * @return
     */
    @Operation(summary = "获取全部品牌")
    @GetMapping("/allBrand")
    public AjaxResult getAllBrand(){
        return success(brandService.getAllBrand());
    }


}
