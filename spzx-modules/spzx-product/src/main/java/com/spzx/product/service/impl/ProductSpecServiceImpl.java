package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.ProductSpec;
import com.spzx.product.mapper.ProductSpecMapper;
import com.spzx.product.service.ICategoryService;
import com.spzx.product.service.IProductSpecService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 3/7/2024 21:00 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Service
public class ProductSpecServiceImpl extends ServiceImpl<ProductSpecMapper, ProductSpec> implements IProductSpecService {


    @Resource
    private ICategoryService categoryService;
    @Resource
    private ProductSpecMapper productSpecMapper;


    @Override
    public IPage<ProductSpec> pageProductSpecQuery(IPage<ProductSpec> productSpecPage, ProductSpec productSpec) {
        LambdaQueryWrapper<ProductSpec> productSpecLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.hasText(productSpec.getSpecName())){
            productSpecLambdaQueryWrapper.eq(ProductSpec::getSpecName,productSpec.getSpecName());

        }
        if(productSpec.getCategoryId()!=null){
            productSpecLambdaQueryWrapper.eq(ProductSpec::getCategoryId,productSpec.getCategoryId());
        }
        IPage<ProductSpec> page = this.page(productSpecPage, productSpecLambdaQueryWrapper);
        return page;
    }

    @Override
    public ProductSpec getProductSpecInfo(Long id) {
        ProductSpec productSpec = baseMapper.selectById(id);
        List<Long> list = categoryService.getCategoryByCategoryId(productSpec.getCategoryId());
        productSpec.setCategoryIdList(list);
        return productSpec;
    }

    @Override
    public List<ProductSpec> getProductSpecListByCategoryId(Long categoryId) {
        LambdaQueryWrapper<ProductSpec> productSpecLambdaQueryWrapper = new LambdaQueryWrapper<>();
        productSpecLambdaQueryWrapper.eq(ProductSpec::getCategoryId,categoryId);
        return productSpecMapper.selectList(productSpecLambdaQueryWrapper);
    }


}
