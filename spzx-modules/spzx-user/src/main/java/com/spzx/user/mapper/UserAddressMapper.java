package com.spzx.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.user.api.domain.UserAddress;

import java.util.List;

/**
 * 用户地址Mapper接口
 *
 * @author lucas
 * @date 2024-07-10
 */
public interface UserAddressMapper extends BaseMapper<UserAddress>
{

    /**
     * 查询用户地址列表
     *
     * @param userAddress 用户地址
     * @return 用户地址集合
     */
    public List<UserAddress> selectUserAddressList(UserAddress userAddress);

}
