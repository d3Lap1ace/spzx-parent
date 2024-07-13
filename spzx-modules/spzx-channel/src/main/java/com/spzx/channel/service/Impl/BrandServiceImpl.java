package com.spzx.channel.service.Impl;

import com.spzx.channel.service.IBrandService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteBrandService;
import com.spzx.product.api.domain.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 13/7/2024 18:45 周六
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Service
public class BrandServiceImpl implements IBrandService {


    @Autowired
    private RemoteBrandService remoteBrandService;
    @Override
    public List<Brand> getBrandAll() {
        R<List<Brand>> brandListResult = remoteBrandService.getBrandAll(SecurityConstants.INNER);
        if(R.FAIL == brandListResult.getCode()){
            throw new ServiceException(brandListResult.getMsg());
        }
        return brandListResult.getData();
    }
}
