package com.spzx.product.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.ProductUnit;
import com.spzx.product.service.IProductUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

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
    @Operation(summary = "分页条件查询接口")
    @GetMapping("/list")
    public TableDataInfo list(
            @Parameter(name = "pageNum", description = "当前页码", required = true)
            @RequestParam(value = "pageNum",defaultValue = "0",required = true) Integer pageNum,

            @Parameter(name = "pageSize", description = "每页记录数", required = true)
            @RequestParam(value = "pageSize",defaultValue = "10",required = true) Integer pageSize,

            @Parameter(name = "productUnit", description = "条件", required = false)
            ProductUnit productUnit) {

        //1 创建page对象，传递当前页 、 每页显示记录数
        Page<ProductUnit> pageParam = new Page<>(pageNum,pageSize);

        //2 封装查询条件
        LambdaQueryWrapper<ProductUnit> wrapper = new LambdaQueryWrapper<>();
        //条件非空判断
        if(StringUtils.hasText(productUnit.getName())) {
            wrapper.eq(ProductUnit::getName,productUnit.getName());
        }
        IPage<ProductUnit> pageModel = productUnitService.page(pageParam, wrapper);

        return getDataTable(pageModel);
    }


    /**
     * 根据id获取商品单位详细信息
     * @param id
     * @return
     */
    @Operation(summary = "根据id获取单位详细信息")
    @GetMapping("/{id}")
    public AjaxResult getDetailById(@PathVariable long id){
        return success(productUnitService.getById(id));
    }


    /**
     * 新增单位
     * @param productUnit
     * @return
     */
    @Operation(summary = "新增单位")
    @PostMapping("/add")
    public AjaxResult addProductUnit(@RequestBody ProductUnit productUnit){
        return toAjax(productUnitService.save(productUnit));
    }


    /**
     * 修改单位信息
     * @param productUnit
     * @return
     */
    @Operation(summary = "修改单位信息")
    @PutMapping("/update")
    public AjaxResult updateProductUnit(@RequestBody ProductUnit productUnit){
        return toAjax(productUnitService.updateById(productUnit));
    }


    @Operation(summary = "删除单位")
    @DeleteMapping("/{ids}")
    public AjaxResult deleteProductUnit(@PathVariable Long[] ids){
        return toAjax(productUnitService.removeBatchByIds(Arrays.asList(ids)));
    }

}
