package com.spzx.product.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.Page;
import com.spzx.product.domain.ProductUnit;


/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 10:25 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

/**
 * 商品单位接口
 */
public interface ProductUnitMapper extends BaseMapper<ProductUnit> {


    IPage<ProductUnit> getProductUnitPage(Page<ProductUnit> pageParam, ProductUnit productUnit);
}
