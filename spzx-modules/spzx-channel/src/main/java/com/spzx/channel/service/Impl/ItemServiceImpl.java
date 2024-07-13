package com.spzx.channel.service.Impl;

import com.alibaba.fastjson.JSON;
import com.spzx.channel.domain.ItemVo;
import com.spzx.channel.service.IItemService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductDetails;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.SkuPrice;
import com.spzx.product.api.domain.vo.SkuStockVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 13/7/2024 21:09 周六
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Service
@Slf4j
public class ItemServiceImpl implements IItemService {

    @Autowired
    private RemoteProductService remoteProductService;

    @Override
    public ItemVo item(Long skuId) {
        ItemVo itemVo = new ItemVo();

        //获取sku信息
        R<ProductSku> productSkuResult = remoteProductService.getProductSku(skuId, SecurityConstants.INNER);
        if (R.FAIL == productSkuResult.getCode()) {
            throw new ServiceException(productSkuResult.getMsg());
        }
        ProductSku productSku = productSkuResult.getData();
        //设置到vo里面
        itemVo.setProductSku(productSku);

        //获取商品信息
        R<Product> productResult =
                remoteProductService.getProduct(productSku.getProductId(), SecurityConstants.INNER);
        Product product = productResult.getData();
        //设置到vo里面
        itemVo.setProduct(product);

        //获取商品最新价格
        R<SkuPrice> skuPriceResult = remoteProductService.getSkuPrice(skuId, SecurityConstants.INNER);
        if (R.FAIL == skuPriceResult.getCode()) {
            throw new ServiceException(skuPriceResult.getMsg());
        }
        SkuPrice skuPrice = skuPriceResult.getData();
        itemVo.setSkuPrice(skuPrice);

        //获取商品详情
        R<ProductDetails> productDetailsResult = remoteProductService.getProductDetails(productSku.getProductId(), SecurityConstants.INNER);
        if (R.FAIL == productDetailsResult.getCode()) {
            throw new ServiceException(productDetailsResult.getMsg());
        }
        ProductDetails productDetails = productDetailsResult.getData();
        itemVo.setDetailsImageUrlList(Arrays.asList(productDetails.getImageUrls().split(",")));

        //获取商品规格对应商品skuId信息
        R<Map<String, Long>> skuSpecValueResult = remoteProductService.getSkuSpecValue(productSku.getProductId(), SecurityConstants.INNER);
        if (R.FAIL == skuSpecValueResult.getCode()) {
            throw new ServiceException(skuSpecValueResult.getMsg());
        }
        Map<String, Long> skuSpecValueMap = skuSpecValueResult.getData();
        itemVo.setSkuSpecValueMap(skuSpecValueMap);

        //获取商品库存信息
        R<SkuStockVo> skuStockResult = remoteProductService.getSkuStock(skuId, SecurityConstants.INNER);
        if (R.FAIL == skuStockResult.getCode()) {
            throw new ServiceException(skuStockResult.getMsg());
        }
        SkuStockVo skuStockVo = skuStockResult.getData();
        itemVo.setSkuStockVo(skuStockVo);

        return itemVo;
    }
}
