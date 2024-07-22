package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.domain.SkuStock;
import org.springframework.data.repository.query.Param;

/**
 * 商品skuMapper接口
 *
 */
public interface SkuStockMapper extends BaseMapper<SkuStock> {


    SkuStock check(@Param("skuId") Long skuId, @Param("num")Integer skuNum);

    Integer lock(@Param("skuId") Long skuId, @Param("num")Integer skuNum);
}