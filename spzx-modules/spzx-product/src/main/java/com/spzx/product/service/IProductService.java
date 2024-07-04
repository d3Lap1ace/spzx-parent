package com.spzx.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.Brand;
import com.spzx.product.domain.Product;

import java.util.List;

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
    IPage<Product> pageProductQuery(Page<Product> productPage, Product product);

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
}
