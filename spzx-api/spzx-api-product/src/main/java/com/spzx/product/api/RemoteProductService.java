package com.spzx.product.api;

import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.api.domain.ProductDetails;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.SkuPrice;
import com.spzx.product.api.domain.SkuQuery;
import com.spzx.product.api.domain.vo.SkuStockVo;
import com.spzx.product.api.factory.RemoteProductFallbackFactory;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 12/7/2024 10:10 周五
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
// @FeignClinet 指定远程调用的接口所在服务名字,设置降级机制
@FeignClient(contextId = "remoteProductSercvice",
        value = ServiceNameConstants.PRODUCT_SERVICE,
        fallbackFactory = RemoteProductFallbackFactory.class)
public interface RemoteProductService {


    @GetMapping("/product/getTopSale")
    R<List<ProductSku>> getTopSale(@RequestHeader(SecurityConstants.FROM_SOURCE)String source);

    @GetMapping("/product/skuList/{pageNum}/{pageSize}")
    R<TableDataInfo> skuList(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, @SpringQueryMap SkuQuery skuQuery, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    @GetMapping("/product/getProductSku/{skuId}")
    public R<ProductSku> getProductSku(@PathVariable("skuId") Long skuId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping("/product/getProduct/{id}")
    public R<Product> getProduct(@PathVariable("id") Long id, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping( "/product/getSkuPrice/{skuId}")
    public R<SkuPrice> getSkuPrice(@PathVariable("skuId") Long skuId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping( "/product/getProductDetails/{id}")
    public R<ProductDetails> getProductDetails(@PathVariable("id") Long id, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping("/product/getSkuSpecValue/{id}")
    public R<Map<String, Long>> getSkuSpecValue(@PathVariable("id") Long id, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping("/product/getSkuStock/{skuId}")
    public R<SkuStockVo> getSkuStock(@PathVariable("skuId") Long skuId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

}
