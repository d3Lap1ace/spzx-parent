package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.*;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        baseMapper.insert(product);

        // 获取商品sku列表
        List<ProductSku> productSkuList = product.getProductSkuList();

        // 商品sku赋值并添加
        for (int i = 0,skuListSize = productSkuList.size(); i < skuListSize; ++i) {
            ProductSku productSku = productSkuList.get(i);
            productSku.setSkuCode(product.getId()+"_"+i);
            productSku.setSkuName(product.getName()+" "+productSku.getSkuSpec());
            productSku.setProductId(product.getId());
            productSkuMapper.insert(productSku);

            // 商品库存赋值并添加
            SkuStock skuStock = new SkuStock();
            skuStock.setSkuId(productSku.getId());
            skuStock.setTotalNum(productSku.getStockNum());
            skuStock.setLockNum(0);
            skuStock.setAvailableNum(productSku.getStockNum());
            skuStock.setSaleNum(0);
            skuStockMapper.insert(skuStock);
        }
        // 商品详解信息赋值并添加
        ProductDetails productDetails = new ProductDetails();
        productDetails.setProductId(product.getId());
        productDetails.setImageUrls(String.join(",", product.getDetailsImageUrlList()));
        productDetailsMapper.insert(productDetails);


        return product.getId().intValue();
    }

    @Override
    public Product getProductById(Long id) {
        // 该商品信息
        Product product = baseMapper.selectById(id);

        // sku列表
        List<ProductSku> productSkuList = product.getProductSkuList();
        LambdaQueryWrapper<ProductSku> productSkuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        productSkuLambdaQueryWrapper.eq(ProductSku::getProductId,id);

        // 查询库存
        List<Long> skuIdList = productSkuList.stream().map(ProductSku::getId).collect(Collectors.toList()); // skuid集合
        List<SkuStock> skuStockList = skuStockMapper.selectList(new LambdaQueryWrapper<SkuStock>()
                .in(SkuStock::getSkuId,skuIdList)
                .select(SkuStock::getSkuId, SkuStock::getTotalNum));
        Map<Long, Integer> skuIdToStockNumMap = skuStockList.stream()
                .collect(Collectors.toMap(SkuStock::getSkuId, SkuStock::getTotalNum));
        productSkuList.forEach(item -> {
            item.setStockNum(skuIdToStockNumMap.get(item.getId()));
        });
        product.setProductSkuList(productSkuList);

        //商品详情
        ProductDetails productDetails = productDetailsMapper.selectOne(new LambdaQueryWrapper<ProductDetails>()
                .eq(ProductDetails::getProductId, id));
        product.setDetailsImageUrlList(Arrays.asList(productDetails.getImageUrls().split(",")));
        return product;

    }

    @Override
    public int updateProduct(Product product) {
        baseMapper.updateById(product);

        List<ProductSku> productSkuList = product.getProductSkuList();
        productSkuList.forEach(productSku -> {
            productSkuMapper.updateById(productSku);

            SkuStock skuStock = skuStockMapper.selectOne(new LambdaQueryWrapper<SkuStock>().eq(SkuStock::getSkuId, productSku.getId()));
            skuStock.setTotalNum(skuStock.getTotalNum());
            skuStock.setAvailableNum(skuStock.getTotalNum()-skuStock.getLockNum());
            skuStockMapper.updateById(skuStock);
        });

        //修改商品详细信息
        ProductDetails productDetails = productDetailsMapper.selectOne(new LambdaQueryWrapper<ProductDetails>().eq(ProductDetails::getProductId, product.getId()));
        productDetails.setImageUrls(String.join(",", product.getDetailsImageUrlList()));
        productDetailsMapper.updateById(productDetails);
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteProductByIds(Long[] ids) {
        baseMapper.deleteBatchIds(Arrays.asList(ids));

        List<ProductSku> productSkuList = productSkuMapper.selectList(new LambdaQueryWrapper<ProductSku>().in(ProductSku::getProductId, ids)
                .select(ProductSku::getId));
        List<Long> skuIdList = productSkuList.stream().map(ProductSku::getId).collect(Collectors.toList());
        productSkuMapper.delete(new LambdaQueryWrapper<ProductSku>().in(ProductSku::getProductId, ids));
        skuStockMapper.delete(new LambdaQueryWrapper<SkuStock>().in(SkuStock::getSkuId, skuIdList));
        productDetailsMapper.delete(new LambdaQueryWrapper<ProductDetails>().in(ProductDetails::getProductId, ids));
        return 1;

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAuditStatus(Long id, Integer auditStatus) {
        Product product = new Product();
        product.setId(id);
        if(auditStatus == 1) {
            product.setAuditStatus(1);
            product.setAuditMessage("审批通过");
        } else {
            product.setAuditStatus(-1);
            product.setAuditMessage("审批不通过");
        }
        baseMapper.updateById(product);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = new Product();
        product.setId(id);
        if(status == 1) {
            product.setStatus(1);
        } else {
            product.setStatus(-1);
        }
        baseMapper.updateById(product);
    }
}
