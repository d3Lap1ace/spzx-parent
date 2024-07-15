package com.spzx.channel.service.Impl;

import com.spzx.channel.service.ICategoryService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteCategoryService;
import com.spzx.product.api.domain.vo.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 13/7/2024 14:26 周六
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private RemoteCategoryService remoteCategoryService;


    @Override
    public List<CategoryVo> tree() {
        R<List<CategoryVo>> categoryVoListResult = remoteCategoryService.tree(SecurityConstants.INNER);
        if (R.FAIL == categoryVoListResult.getCode()) {
            throw new ServiceException(categoryVoListResult.getMsg());
        }
        return categoryVoListResult.getData();
    }
}
