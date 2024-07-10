package com.spzx.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.user.domain.Region;
import com.spzx.user.domain.UserAddress;
import com.spzx.user.mapper.RegionMapper;
import com.spzx.user.mapper.UserAddressMapper;
import com.spzx.user.service.IRegionService;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 10/7/2024 16:13 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Service
public class RegionServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements IRegionService {

    @Resource
    private RegionMapper regionMapper;

    @Override
    public List<Region> treeSelect(String parentCode) {
        return regionMapper.selectList(new LambdaQueryWrapper<Region>().eq(Region::getParentCode,parentCode));
    }
}
