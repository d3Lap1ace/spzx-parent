package com.spzx.channel.service;

import com.spzx.channel.domain.ItemVo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 13/7/2024 21:08 周六
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
public interface IItemService {


    //远程调用商品微服务接口之前 提前知道用户访问商品SKUID是否存在与布隆过滤器

    ItemVo item(Long skuId);
}
