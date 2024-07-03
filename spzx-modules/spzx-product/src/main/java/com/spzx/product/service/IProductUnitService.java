package com.spzx.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.Page;
import com.spzx.product.domain.ProductUnit;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 10:19 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
public interface IProductUnitService extends IService<ProductUnit> {

    /**
     * 获取分页列表
     * @param pageParam
     * @param productUnit
     * @return
     */
    IPage<ProductUnit> getProductUnitPage(Page<ProductUnit> pageParam, ProductUnit productUnit);

    /**
     * 获取商品单位详细信息
     * @param id
     * @return
     */
    ProductUnit getDetailById(long id);

    /**
     * 新增单位
     * @param productUnit
     * @return
     */
    int addProductUnit(ProductUnit productUnit);


    /**
     * 修改单位信息
     * @param productUnit
     * @return
     */
    int updateProductUnit(ProductUnit productUnit);

    /**
     * 删除单位信息
     * @param ids
     * @return
     */
    int deleteProductUnitById(Long[] ids);
}
