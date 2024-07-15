package com.spzx.channel.service.Impl;

import com.spzx.channel.service.IIndexService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteCategoryService;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.vo.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 12/7/2024 11:55 周五
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Service
public class IndexServiceImpl implements IIndexService {

    @Autowired
    private RemoteCategoryService categoryService;

    @Autowired
    private RemoteProductService productService;

    @Override
    public Map<String, Object> getIndexData() {
        // 获取分类视图对象
        R<List<CategoryVo>> categoryResultMsg = categoryService.getOneCategory(SecurityConstants.INNER);
        // 获取失败
        if(R.FAIL == categoryResultMsg.getCode()){
            throw new ServiceException(categoryResultMsg.getMsg());
        }
        // 获取销量顶端数据
        R<List<ProductSku>> topSaleResultMsg = productService.getTopSale(SecurityConstants.INNER);
        // 获取失败
        if(R.FAIL == topSaleResultMsg.getCode()){
            throw new ServiceException(topSaleResultMsg.getMsg());
        }

        // 声明hashmap 并放入对象 返回
        HashMap<String, Object> map = new HashMap<>();
        map.put("categoryResultMsg",categoryResultMsg);
        map.put("topSaleResultMsg",topSaleResultMsg);
        return map;
    }
}
