package com.spzx.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.ProductSpec;
import com.spzx.product.service.IProductSpecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 20:53 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
@Tag(name = "商品规格接口管理")
@RestController
@RequestMapping("/productSpec")
public class ProductSpecController extends BaseController {

    @Autowired
    public IProductSpecService productSpecService;

    /**
     * 分页查询商品规格列表
     * @param current
     * @param size
     * @param productSpec
     * @return
     */
    @Operation(summary = "分页查询商品规格列表")
    @GetMapping("/list")
    public TableDataInfo getPageList(@Parameter(name = "current", description = "当前页码", required = true)
                                     @RequestParam(value = "current",defaultValue = "1",required = true) Integer current,
                                     @Parameter(name = "size", description = "每页记录数", required = true)
                                     @RequestParam (value= "size",defaultValue = "5",required = true) Integer size,
                                     ProductSpec productSpec){
        IPage<ProductSpec> productSpecPage = new Page<>(current, size);
        IPage<ProductSpec> productSpeclist = productSpecService.pageProductSpecQuery(productSpecPage,productSpec);
        return getDataTable(productSpeclist);
    }


    /**
     * 根据id获取商品规格详细信息
     * @param id
     * @return
     */
    @Operation(summary = "获取商品规格详细信息")
    @GetMapping("/{id}")
    public AjaxResult getProductSpecInfo(@PathVariable Long id ){
        return success(productSpecService.getProductSpecInfo(id));

    }


    /**
     * 新增商品规格
     * @param productSpec
     * @return
     */
    @Operation(summary = "新增商品规格")
    @PostMapping
    public AjaxResult addProductSpec(@RequestBody ProductSpec productSpec){
        productSpec.setCreateBy(SecurityUtils.getUsername());
        return toAjax(productSpecService.save(productSpec));
    }

    /**
     * 修改商品规格
     * @param productSpec
     * @return
     */
    @Operation(summary = "修改商品规格")
    @PutMapping("/update")
    public AjaxResult updateProductSpec(@RequestBody ProductSpec productSpec){
        return toAjax(productSpecService.updateById(productSpec));
    }

    /**
     * 删除商品规格
     * @param ids
     * @return
     */
    @Operation(summary = "删除商品规格")
    @DeleteMapping("/{ids}")
    public AjaxResult deleteProductSpec(@PathVariable Long[] ids){
        return toAjax(productSpecService.removeBatchByIds(Arrays.asList(ids)));
    }


    /**
     * 根据分类id获取商品规格列表
     * @param categoryId
     * @return
     */
    @Operation(summary = "根据分类id获取商品规格列表")
    @GetMapping("/productSpecList/{categoryId}")
    public AjaxResult getProductSpecListByCategoryId(@PathVariable Long categoryId) {
        return success(productSpecService.getProductSpecListByCategoryId(categoryId));
    }


}
