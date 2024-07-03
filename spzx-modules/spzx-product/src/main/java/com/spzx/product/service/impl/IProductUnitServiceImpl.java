package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.spzx.product.domain.ProductUnit;
import com.spzx.product.mapper.ProductUnitMapper;
import com.spzx.product.service.IProductUnitService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 10:19 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Service
public class IProductUnitServiceImpl extends ServiceImpl<ProductUnitMapper,ProductUnit> implements IProductUnitService {

    @Resource
    private ProductUnitMapper productUnitMapper;
    @Override
    public IPage<ProductUnit> getProductUnitPage(Page<ProductUnit> pageParam, ProductUnit productUnit) {
        return productUnitMapper.getProductUnitPage(pageParam,productUnit);
    }

    @Override
    public ProductUnit getDetailById(long id) {
        return this.getById(id);
    }

    @Override
    public int addProductUnit(ProductUnit productUnit) {
        return this.save(productUnit)? 1:0;
    }

    @Override
    public int updateProductUnit(ProductUnit productUnit) {
        return this.updateById(productUnit)?1:0;
    }

    @Override
    public int deleteProductUnitById(Long[] ids) {
        return this.removeBatchByIds(Arrays.asList(ids))?1:0;
    }
}
