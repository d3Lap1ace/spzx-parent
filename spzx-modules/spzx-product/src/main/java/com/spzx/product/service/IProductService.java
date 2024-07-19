package com.spzx.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.api.domain.*;
import com.spzx.product.api.domain.vo.SkuStockVo;

import java.util.List;
import java.util.Map;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 4/7/2024 09:55 周四
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
public interface IProductService extends IService<Product> {


    /**
     * 分页查询商品列表
     * @param productPage
     * @param product
     * @return
     */
    IPage<Product> selectProductList(Page<Product> productPage, Product product);

    /**
     * 新增商品
     * @param product
     * @return
     */
    int insertProduct(Product product);

    /**
     * 查询商品
     * @param id
     * @return
     */
    Product getProductById(Long id);

    /**
     * 修改商品
     * @param product
     * @return
     */
    int updateProduct(Product product);

    /**
     * 删除商品
     * @param ids
     * @return
     */
    int deleteProductByIds(Long[] ids);

    /**
     * 商品审核
     * @param id
     * @param auditStatus
     */
    void updateAuditStatus(Long id, Integer auditStatus);

    /**
     * 跟新上下架状态
     * @param id
     * @param status
     */
    void updateStatus(Long id, Integer status);


    /**
     * 获取销量好的sku
     * @return
     */
    List<ProductSku> getTopSale();


    List<ProductSku> selectProductSkuList(SkuQuery skuQuery);


    ProductSku getProductSku(Long skuId);

    Product getProduct(Long id);

    SkuPrice getSkuPrice(Long skuId);

    ProductDetails getProductDetails(Long id);

    Map<String, Long> getSkuSpecValue(Long id);

    SkuStockVo getSkuStock(Long skuId);

    List<SkuPrice> getSkuPriceList(List<Long> skuIdList);
}
