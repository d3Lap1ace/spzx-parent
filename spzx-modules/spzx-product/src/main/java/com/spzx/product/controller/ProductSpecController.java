package com.spzx.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.ProductSpec;
import com.spzx.product.service.IProductSpecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @Operation(summary = "分页查询商品规格列表")
    @GetMapping("/list")
    public TableDataInfo getPageList(@Parameter(name = "current", description = "当前页码", required = true)
                                         @RequestParam Integer current,
                                     @Parameter(name = "size", description = "每页记录数", required = true)
                                     @RequestParam Integer size,
                                     ProductSpec productSpec){
        IPage<ProductSpec> productSpecPage = new Page<>(current, size);
        IPage<ProductSpec> productSpeclist = productSpecService.page(productSpecPage);
        return getDataTable(productSpeclist);
    }



}
