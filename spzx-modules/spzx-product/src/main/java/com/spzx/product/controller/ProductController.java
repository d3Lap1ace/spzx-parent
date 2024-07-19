package com.spzx.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.product.api.domain.*;
import com.spzx.product.api.domain.vo.SkuStockVo;
import com.spzx.product.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public TableDataInfo list(@RequestParam (value = "current",defaultValue = "1",required = true)Integer current,
                                          @RequestParam (value = "size",defaultValue = "5",required = true)Integer size,
                                          Product product){
        Page<Product> productPage = new Page<>(current,size);
        IPage<Product> productList = productService.selectProductList(productPage,product);
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

    /**
     * 修改商品
     * @param product
     * @return
     */
    @Operation(summary = "修改商品")
    @PutMapping
    public AjaxResult updateProduct(@RequestBody Product product) {
        return toAjax(productService.updateProduct(product));
    }


    /**
     * 删除商品
     *
     * @param ids
     * @return
     */
    @Operation(summary = "删除商品")
    @DeleteMapping("/{ids}")
    public AjaxResult deleteProductByIds(@PathVariable Long[] ids) {
        return toAjax(productService.deleteProductByIds(ids));
    }


    /**
     * 商品审核
     * @param id
     * @param auditStatus
     * @return
     */
    @Operation(summary = "商品审核")
    @GetMapping("updateAuditStatus/{id}/{auditStatus}")
    public AjaxResult updateAuditStatus(@PathVariable Long id, @PathVariable Integer auditStatus) {
        productService.updateAuditStatus(id, auditStatus);
        return success();
    }

    /**
     * 更新上下架状态
     * @param id
     * @param status
     * @return
     */
    @Operation(summary = "更新上下架状态")
    @GetMapping("updateStatus/{id}/{status}")
    public AjaxResult updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        productService.updateStatus(id, status);
        return success();
    }

    /**
     * 获取销量好的sku
     * @return
     */
    @InnerAuth
    @Operation(summary = "获取销量好的sku")
    @GetMapping("getTopSale")
    public R<List<ProductSku>> getTopSale() {
        return R.ok(productService.getTopSale());
    }

    @InnerAuth
    @GetMapping("/skuList/{pageNum}/{pageSize}")
    public R<TableDataInfo> skuList(
            @Parameter(name = "pageNum", description = "当前页码", required = true)
            @PathVariable Integer pageNum,
            @Parameter(name = "pageSize", description = "每页记录数", required = true)
            @PathVariable Integer pageSize,
            @Parameter(name = "productQuery", description = "查询对象", required = false)
            @ModelAttribute SkuQuery skuQuery) {
        PageHelper.startPage(pageNum, pageSize);
        List<ProductSku> list = productService.selectProductSkuList(skuQuery);
        return R.ok(getDataTable(list));
    }

    @Operation(summary = "获取商品sku信息")
    @InnerAuth
    @GetMapping(value = "/getProductSku/{skuId}")
    public R<ProductSku> getProductSku(@PathVariable("skuId") Long skuId)
    {
        return R.ok(productService.getProductSku(skuId));
    }

    @Operation(summary = "获取商品信息")
    @InnerAuth
    @GetMapping(value = "/getProduct/{id}")
    public R<Product> getProduct(@PathVariable("id") Long id)
    {
        return R.ok(productService.getProduct(id));
    }

    @Operation(summary = "获取商品sku最新价格信息")
    @InnerAuth
    @GetMapping(value = "/getSkuPrice/{skuId}")
    public R<SkuPrice> getSkuPrice(@PathVariable("skuId") Long skuId)
    {
        return R.ok(productService.getSkuPrice(skuId));
    }

    @Operation(summary = "获取商品详细信息")
    @InnerAuth
    @GetMapping(value = "/getProductDetails/{id}")
    public R<ProductDetails> getProductDetails(@PathVariable("id") Long id)
    {
        return R.ok(productService.getProductDetails(id));
    }

    @Operation(summary = "获取商品sku规则详细信息")
    @InnerAuth
    @GetMapping(value = "/getSkuSpecValue/{id}")
    public R<Map<String, Long>> getSkuSpecValue(@PathVariable("id") Long id)
    {
        return R.ok(productService.getSkuSpecValue(id));
    }

    @Operation(summary = "获取商品sku库存信息")
    @InnerAuth
    @GetMapping(value = "/getSkuStock/{skuId}")
    public R<SkuStockVo> getSkuStock(@PathVariable("skuId") Long skuId)
    {
        return R.ok(productService.getSkuStock(skuId));
    }

    @Operation(summary = "批量获取商品sku最新价格信息")
    @InnerAuth
    @PostMapping(value = "/getSkuPriceList")
    public R<List<SkuPrice>> getSkuPriceList(@RequestBody List<Long> skuIdList)
    {
        return R.ok(productService.getSkuPriceList(skuIdList));
    }


}
