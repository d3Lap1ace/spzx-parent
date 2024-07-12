package com.spzx.product.api;


import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.domain.vo.CategoryVo;
import com.spzx.product.api.factory.RemoteCategoryFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * 文件服务
 *
 * @author ruoyi
 */
// @FeignClinet 指定远程调用的接口所在服务名字,设置降级机制
// contextid 指定id名称  非必
// value 指定接口在那服务  必须
// fallback 设置降级     必须   服务类
// fallbackfactory 降级工厂   必须
@FeignClient(contextId = "remoteCategoryService",
        value = ServiceNameConstants.PRODUCT_SERVICE,
        fallbackFactory = RemoteCategoryFallbackFactory.class)
public interface RemoteCategoryService{


    @GetMapping(value = "/category/getOneCategory")
    public R<List<CategoryVo>> getOneCategory(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}