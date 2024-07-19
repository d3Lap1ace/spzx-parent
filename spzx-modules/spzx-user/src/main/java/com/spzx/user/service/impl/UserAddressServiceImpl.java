package com.spzx.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.user.api.domain.UserAddress;
import com.spzx.user.mapper.UserAddressMapper;
import com.spzx.user.service.IUserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 用户地址Service业务层处理
 *
 * @author lucas
 * @date 2024-07-10
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements IUserAddressService
{
    @Autowired
    private UserAddressMapper userAddressMapper;
    @Autowired
    private RegionServiceImpl regionService;

    /**
     * 查询用户地址列表
     *
     * @param userAddress 用户地址
     * @return 用户地址
     */
    @Override
    public List<UserAddress> selectUserAddressList(UserAddress userAddress)
    {
        return userAddressMapper.selectUserAddressList(userAddress);
    }

    /**
     * 查询用户地址列表
     *
     * @param
     * @return 用户地址
     */
    @Override
    public List<UserAddress> selectUserAddressList() {
        Long userId = SecurityContextHolder.getUserId();
        return userAddressMapper.selectList(new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId,userId));
    }

    /**
     * 新增用户地址
     *
     * @param userAddress 用户地址
     * @return 结果
     */
    @Override
    public int insertUserAddress(UserAddress userAddress) {
        userAddress.setUserId(SecurityContextHolder.getUserId());
        String provinceName = regionService.getNameByCode(userAddress.getProvinceCode());
        String cityName = regionService.getNameByCode(userAddress.getCityCode());
        String districtName = regionService.getNameByCode(userAddress.getDistrictCode());
        String fullAddress = provinceName + cityName + districtName + userAddress.getAddress();
        userAddress.setFullAddress(fullAddress);
        userAddress.setCreateTime(new Date());
        userAddress.setCreateBy(SecurityContextHolder.getUserName());

        // 如果是默认地址，其他地址更新为非默认地址
        if(userAddress.getIsDefault().intValue() == 1){
            UserAddress updateUserAddress = new UserAddress();
            updateUserAddress.setIsDefault(0L);
            userAddressMapper.update(updateUserAddress,new LambdaQueryWrapper<UserAddress>()
                    .eq(UserAddress::getUserId,userAddress.getUserId()));
        }
        return userAddressMapper.insert(userAddress);
    }

    /**
     * 修改用户地址
     *
     * @param userAddress 用户地址
     * @return 结果
     */
    @Override
    public int updateUserAddress(UserAddress userAddress) {
        String provinceName = regionService.getNameByCode(userAddress.getProvinceCode());
        String cityName = regionService.getNameByCode(userAddress.getCityCode());
        String districtName = regionService.getNameByCode(userAddress.getDistrictCode());
        String fullAddress = provinceName + cityName + districtName + userAddress.getAddress();
        userAddress.setFullAddress(fullAddress);
        userAddress.setUpdateTime(new Date());
        userAddress.setUpdateBy(SecurityContextHolder.getUserName());
        //如果是默认地址，其他地址更新为非默认地址
        if(userAddress.getIsDefault().intValue() == 1){
            UserAddress updateUserAddress = new UserAddress();
            updateUserAddress.setIsDefault(0L);
            userAddressMapper.update(updateUserAddress,new LambdaQueryWrapper<UserAddress>()
                    .eq(UserAddress::getAddress,userAddress.getAddress()));
        }
        return userAddressMapper.updateById(userAddress);
    }

}
