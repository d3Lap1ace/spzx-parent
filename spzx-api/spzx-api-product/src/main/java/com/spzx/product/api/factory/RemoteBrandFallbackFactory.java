package com.spzx.product.api.factory;

import com.spzx.common.core.domain.R;
import com.spzx.product.api.RemoteBrandService;
import com.spzx.product.api.domain.Brand;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 13/7/2024 18:34 周六
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Component
public class RemoteBrandFallbackFactory implements FallbackFactory<RemoteBrandService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteBrandFallbackFactory.class);
    @Override
    public RemoteBrandService create(Throwable cause) {
        return new RemoteBrandService() {
            @Override
            public R<List<Brand>> getBrandAll(String source) {
                return R.fail("获取全部品牌失败:" + cause.getMessage());
            }
        };
    }
}
