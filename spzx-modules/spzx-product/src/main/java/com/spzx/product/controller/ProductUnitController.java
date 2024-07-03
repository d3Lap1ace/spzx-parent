package com.spzx.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.Page;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.ProductUnit;
import com.spzx.product.service.IProductUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 10:06 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Tag(name ="商品单位接口管理")
@RestController
@RequestMapping("/productUnit")
public class ProductUnitController extends BaseController {

    @Autowired
    private IProductUnitService productUnitService;

    /**
     * 分页列表
     * @param pageNum
     * @param pageSize
     * @param productUnit
     * @return
     */
    @Operation(summary = "获取分页列表")
    @GetMapping("/list")
    public TableDataInfo getPage(@Parameter(name = "pageNum",description = "当前页码",required = true)
                                 @RequestParam(value = "pageNum",defaultValue = "0",required = true)Integer pageNum,
                                 @Parameter(name = "pageSize",description = "每页记录数",required = true)
                                 @RequestParam(value = "pageSize",defaultValue = "0",required = true)Integer pageSize,
                                 @Parameter(name = "driverInfoQuery",description = "查询对象",required = false)ProductUnit productUnit){
        Page<ProductUnit> pageParam = new Page<>(pageNum, pageSize);
        IPage<ProductUnit> iPage = productUnitService.getProductUnitPage(pageParam,productUnit);
        return getDataTable(iPage);
    }


    /**
     * 获取商品单位详细信息
     * @param id
     * @return
     */
    @Operation(summary = "获取单位详细信息")
    @GetMapping("/{id}")
    public AjaxResult getDetailById(@PathVariable long id){
        return AjaxResult.success(productUnitService.getDetailById(id));
    }


    /**
     * 新增单位
     * @param productUnit
     * @return
     */
    @Operation(summary = "新增单位")
    @PostMapping
    public AjaxResult addProductUnit(@RequestBody ProductUnit productUnit){
        return toAjax(productUnitService.addProductUnit(productUnit));
    }


    /**
     * 修改单位信息
     * @param productUnit
     * @return
     */
    @Operation(summary = "修改单位信息")
    @PutMapping
    public AjaxResult updateProductUnit(@RequestBody ProductUnit productUnit){
        productUnit.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(productUnitService.updateProductUnit(productUnit));
    }


    @Operation(summary = "删除单位")
    @DeleteMapping("/{ids}")
    public AjaxResult deleteProductUnit(@PathVariable Long[] ids){
        return toAjax(productUnitService.deleteProductUnitById(ids));
    }

}
