package com.spzx.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.user.api.domain.UserAddress;

import java.util.List;

/**
 * 用户地址Service接口
 *
 * @author lucas
 * @date 2024-07-10
 */
public interface IUserAddressService extends IService<UserAddress>
{

    /**
     * 查询用户地址列表
     *
     * @param userAddress 用户地址
     * @return 用户地址集合
     */
    public List<UserAddress> selectUserAddressList(UserAddress userAddress);

    /**
     * 查询用户地址列表
     *
     * @return 用户地址集合
     */
    public List<UserAddress> selectUserAddressList();

    /**
     * 新增用户地址
     *
     * @param userAddress 用户地址
     * @return 结果
     */
    public int insertUserAddress(UserAddress userAddress);

    /**
     * 修改用户地址
     *
     * @param userAddress 用户地址
     * @return 结果
     */
    public int updateUserAddress(UserAddress userAddress);

}
