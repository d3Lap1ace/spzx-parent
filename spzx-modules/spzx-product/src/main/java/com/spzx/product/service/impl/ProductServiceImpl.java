package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.utils.bean.BeanUtils;
import com.spzx.common.core.utils.uuid.UUID;
import com.spzx.common.redis.cache.RedisCache;
import com.spzx.product.api.domain.*;
import com.spzx.product.api.domain.vo.SkuStockVo;
import com.spzx.product.domain.*;
import com.spzx.product.mapper.ProductDetailsMapper;
import com.spzx.product.mapper.ProductMapper;
import com.spzx.product.mapper.ProductSkuMapper;
import com.spzx.product.mapper.SkuStockMapper;
import com.spzx.product.service.IProductService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 4/7/2024 09:56 周四
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Resource
    private ProductSkuMapper productSkuMapper;
    @Resource
    private ProductDetailsMapper productDetailsMapper;
    @Resource
    private SkuStockMapper skuStockMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;


    @Override
    public IPage<Product> selectProductList(Page<Product> productPage, Product product) {
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
        for (int i = 0, size = productSkuList.size(); i < size; i++) {
            ProductSku productSku = productSkuList.get(i);
            productSku.setSkuCode(product.getId() + "_" + i);
            productSku.setProductId(product.getId());
            String skuName = product.getName() + " " + productSku.getSkuSpec();
            productSku.setSkuName(skuName);
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
        LambdaQueryWrapper<ProductSku> productSkuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        productSkuLambdaQueryWrapper.eq(ProductSku::getProductId,id);
        List<ProductSku> productSkuList = productSkuMapper.selectList(productSkuLambdaQueryWrapper);

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

            //sku加入布隆过滤器
            RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("sku:bloom:filter");
            List<ProductSku> productSkuList = productSkuMapper.selectList(new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getProductId, id));
            productSkuList.forEach(item -> {
                bloomFilter.add(item.getId());
            });
        } else {
            product.setStatus(-1);
        }
        baseMapper.updateById(product);
    }

    @Override
    public List<ProductSku> getTopSale() {
        return productSkuMapper.selectTopSale();
    }

    @Override
    public List<ProductSku> selectProductSkuList(SkuQuery skuQuery) {
        return productSkuMapper.selectProductSkuList(skuQuery);
    }

    /**
     * aop+redis分布式锁
     * @param skuId
     * @return
     */
    @Override
    public ProductSku getProductSku(Long skuId) {
        // 根据skuid  从redis中查询缓存
        String dataKey = "product:sku:"+skuId;
        ProductSku productSku  = (ProductSku)redisTemplate.opsForValue().get(dataKey);
        // 命中缓存 直接返回
        if(productSku!=null) {
            log.info("命中缓存,直接返回,线程ID:{}",Thread.currentThread().getName());
            return productSku;
        }
        // 缓存没有 从mysql中查找
        // 添加分布式锁
        RLock lock = redissonClient.getLock("product:sku:"+skuId);
        // 加入分布式锁
        try {
            boolean flag = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if(flag){
                try {
                    // 执行业务逻辑 并把查找出来的数据放到redis里面
                    ProductSku productSkuFromDB = this.getProductSkuFromDB(skuId);
                    redisTemplate.opsForValue().set("datakey",productSkuFromDB,10,TimeUnit.SECONDS);
                    return productSkuFromDB;
                } finally {
                    lock.unlock();
                }
            }else {
                // 获取锁失败 进行自旋
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return this.getProductSku(skuId);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * redis分布式锁
     * @param skuId
     * @return
     */
    public ProductSku getProductSku1(Long skuId) {
        try {
            //1.优先从缓存中获取数据
            //1.1 构建业务数据Key 形式：前缀+业务唯一标识
            String datakey = "product:sku:"+skuId;
            //1.2 查询Redis获取业务数据
            ProductSku productSku = (ProductSku) redisTemplate.opsForValue().get(datakey);
            //1.3 命中缓存则直接返回
            if(productSku != null){
                log.info("命中缓存，直接返回，线程ID：{}，线程名称：{}",Thread.currentThread().getName());
                return productSku;
            }
            //2.尝试获取分布式锁（set k v ex nx可能获取锁失败）
            //2.1 构建锁key
            String lockKey = "product:sku:lock:" + skuId;
            //2.2 采用UUID作为线程标识
            String uuid = UUID.randomUUID().toString().replace("-","");
            //2.3 利用Redis提供set nx ex 获取分布式锁
            Boolean flag = redisTemplate.opsForValue().setIfAbsent(lockKey, uuid, 5, TimeUnit.SECONDS);
            if(flag){
                //3.获取锁成功执行业务,将查询业务数据放入缓存Redis
                try {
                    log.info("获取锁成功：{}，线程名称：{}", Thread.currentThread().getId(), Thread.currentThread().getName());
                    productSku = this.getProductSkuFromDB(skuId);
                    return productSku;
                } finally {
                    //4.业务执行完毕释放锁
                    String scriptText = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                            "then\n" +
                            "    return redis.call(\"del\",KEYS[1])\n" +
                            "else\n" +
                            "    return 0\n" +
                            "end";
                    DefaultRedisScript<Long> script = new DefaultRedisScript<>();
                    script.setScriptText(scriptText);
                    script.setResultType(Long.class);
                    redisTemplate.execute(script,Arrays.asList(lockKey),uuid);
                }
            }else {
                //5.获取锁失败则自旋（业务要求必须执行）
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.error("获取锁失败，自旋：{}，线程名称：{}", Thread.currentThread().getId(), Thread.currentThread().getName());
                return this.getProductSku(skuId);
            }
        } catch (RuntimeException e) {
            //兜底处理方案：Redis服务有问题，将业务数据获取自动从数据库获取
            log.error("[商品服务]查询商品信息异常：{}", e);
            return this.getProductSkuFromDB(skuId);
        }
    }



    public ProductSku getProductSkuFromDB(Long skuId) {
        return productSkuMapper.selectById(skuId);
    }

    @RedisCache(prefix = "product:")
    @Override
    public Product getProduct(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public SkuPrice getSkuPrice(Long skuId) {

        ProductSku productSku = productSkuMapper.selectOne(new LambdaQueryWrapper<ProductSku>()
                .eq(ProductSku::getId, skuId)
                .select(ProductSku::getSalePrice, ProductSku::getMarketPrice));
        SkuPrice skuPrice = new SkuPrice();
        BeanUtils.copyProperties(productSku,skuPrice);
        return skuPrice;
    }

    @RedisCache(prefix = "productDetails:")
    @Override
    public ProductDetails getProductDetails(Long id) {
        return productDetailsMapper.selectOne(new LambdaQueryWrapper<ProductDetails>().eq(ProductDetails::getProductId, id));
    }

    @RedisCache(prefix = "skuSpecValue:")
    @Override
    public Map<String, Long> getSkuSpecValue(Long id) {
        List<ProductSku> productSkuList = productSkuMapper.selectList(new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getProductId, id).select(ProductSku::getId, ProductSku::getSkuSpec));
        Map<String,Long> skuSpecValueMap = new HashMap<>();
        productSkuList.forEach(item -> {
            skuSpecValueMap.put(item.getSkuSpec(), item.getId());
        });
        return skuSpecValueMap;
    }

    @Override
    public SkuStockVo getSkuStock(Long skuId) {
        SkuStock skuStock = skuStockMapper.selectOne(new LambdaQueryWrapper<SkuStock>().eq(SkuStock::getSkuId, skuId));
        SkuStockVo skuStockVo = new SkuStockVo();
        BeanUtils.copyProperties(skuStock, skuStockVo);
        return skuStockVo;
    }

    @Override
    public List<SkuPrice> getSkuPriceList(List<Long> skuIdList) {
        List<ProductSku> productSkuList = productSkuMapper.selectList(new LambdaQueryWrapper<ProductSku>().in(ProductSku::getId, skuIdList).select(ProductSku::getSalePrice, ProductSku::getId, ProductSku::getSalePrice));
        return productSkuList.stream().map(it -> {
            SkuPrice skuPrice = new SkuPrice();
            skuPrice.setSkuId(it.getId());
            skuPrice.setSalePrice(it.getSalePrice());
            return skuPrice;
        }).collect(Collectors.toList());
    }
}
