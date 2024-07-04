package com.spzx.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.Product;
import com.spzx.product.domain.ProductSpec;
import com.spzx.product.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 4/7/2024 09:54 周四
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Tag(name = "商品管理接口")
@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Autowired
    private IProductService productService;


    /**
     * 分页查询商品列表
     * @param current
     * @param size
     * @param product
     * @return
     */
    @Operation(summary = "分页查询商品列表")
    @GetMapping("/list")
    public TableDataInfo pageProductQuery(@RequestParam Integer current,
                                          @RequestParam Integer size,
                                          Product product){
        Page<Product> productPage = new Page<>(current,size);
        IPage<Product> productList = productService.pageProductQuery(productPage,product);
        return getDataTable(productList);
    }

    /**
     * 新增商品
     *
     * @param product
     * @return
     */
    @Operation(summary = "新增商品")
    @PostMapping
    public AjaxResult add(@RequestBody Product product) {
        return toAjax(productService.insertProduct(product));
    }

    /**
     * 获取商品详细信息
     */
    @Operation(summary = "获取商品详细信息")
    @GetMapping("/{id}")
    public AjaxResult getProductById(@PathVariable Long id) {
        return success(productService.getProductById(id));
    }


}
