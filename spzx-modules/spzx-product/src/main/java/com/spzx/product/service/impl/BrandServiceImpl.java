package com.spzx.product.service.impl;


import com.spzx.product.domain.Brand;
import com.spzx.product.mapper.BrandMapper;
import com.spzx.product.service.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 品牌Service业务层处理
 */
@Service
public class BrandServiceImpl implements IBrandService {
    
    @Autowired
    private BrandMapper brandMapper;


    /**
     * 查询分类品牌列表
     *
     * @param brand 分类品牌
     * @return 分类品牌
     */
    @Override
    public List<Brand> selectBrandList() {
        return brandMapper.selectBrandList();
    }


    /**
     * 获取品牌详细信息
     * @param id
     * @return
     */
    @Override
    public Brand getBrandInfo(Long id) {
        return brandMapper.getBrandInfo(id);
    }

    @Override
    public int insertBrand(Brand brand) {
        return brandMapper.insertBrand(brand);
    }

}