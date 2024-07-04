package com.spzx.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.ProductSpec;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 20:55 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
public interface IProductSpecService extends IService<ProductSpec> {

    /**
     * 根据id获取商品规格详细信息
     * @param id
     * @return
     */
    ProductSpec getProductSpecInfo(Long id);

    /**
     * 分页查询商品规格列表
     * @param productSpecPage
     * @param productSpec
     * @return
     */
    IPage<ProductSpec> pageProductSpecQuery(IPage<ProductSpec> productSpecPage, ProductSpec productSpec);

    /**
     * 根据分类id获取商品规格列表
     * @param categoryId
     * @return
     */
    List<ProductSpec> getProductSpecListByCategoryId(Long categoryId);
}
