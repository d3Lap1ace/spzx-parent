package com.spzx.channel.service;

import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.api.domain.SkuQuery;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 13/7/2024 19:02 周六
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
public interface IListService {
    TableDataInfo selectProductSkuList(Integer pageNum, Integer pageSize, SkuQuery skuQuery);
}
