package com.spzx.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.user.domain.Region;


import java.util.List;


/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 10/7/2024 16:12 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
public interface IRegionService extends IService<UserAddress> {
    List<Region> treeSelect(String parentCode);

    String getNameByCode(String code);
}
