package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.Brand;
import com.spzx.product.domain.Product;
import com.spzx.product.domain.ProductDetails;
import com.spzx.product.mapper.ProductDetailsMapper;
import com.spzx.product.mapper.ProductMapper;
import com.spzx.product.mapper.ProductSkuMapper;
import com.spzx.product.mapper.SkuStockMapper;
import com.spzx.product.service.IProductService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 4/7/2024 09:56 周四
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Resource
    private ProductSkuMapper productSkuMapper;
    @Resource
    private ProductDetailsMapper productDetailsMapper;
    @Resource
    private SkuStockMapper skuStockMapper;

    @Override
    public IPage<Product> pageProductQuery(Page<Product> productPage, Product product) {
        LambdaQueryWrapper<Product> productLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.hasText(product.getName())) productLambdaQueryWrapper.eq(Product::getName,product.getName());
        if(product.getBrandId()!=null) productLambdaQueryWrapper.eq(Product::getBrandId,product.getBrandId());
        if(product.getCategory1Id()!=null) productLambdaQueryWrapper.eq(Product::getCategory1Id,product.getCategory1Id());
        if(product.getCategory2Id()!=null)productLambdaQueryWrapper.eq(Product::getCategory2Id,product.getCategory2Id());
        if(product.getCategory3Id()!=null)productLambdaQueryWrapper.eq(Product::getCategory3Id,product.getCategory3Id());
        IPage<Product> page = this.page(productPage, productLambdaQueryWrapper);
        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertProduct(Product product) {
        return 0;
    }

    @Override
    public Product getProductById(Long id) {
        Product product = baseMapper.selectById(id);
        return null;
    }
}
