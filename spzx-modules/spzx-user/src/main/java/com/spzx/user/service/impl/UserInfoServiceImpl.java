package com.spzx.user.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spzx.user.mapper.UserInfoMapper;
import com.spzx.user.service.IUserInfoService;

/**
 * 会员Service业务层处理
 *
 * @author lucas
 * @date 2024-07-10
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService
{
    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 查询会员列表
     *
     * @param userInfo 会员
     * @return 会员
     */
    @Override
    public List<UserInfo> selectUserInfoList(UserInfo userInfo)
    {
        return userInfoMapper.selectUserInfoList(userInfo);
    }

    @Override
    public void register(UserInfo userInfo) {
        Long count = userInfoMapper.selectCount(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUsername, userInfo.getUsername()));
        if(count > 0){
            throw new ServiceException("手机号已经存在");
        }
        userInfo.setStatus(1);
        userInfo.setSex(0);
        userInfo.setAvatar("https://tse1-mm.cn.bing.net/th/id/OIP-C.kaE-V0auJ8Y0tKMJrmvntgHaHd?rs=1&pid=ImgDetMain");

        // 数据库插入
        userInfoMapper.insert(userInfo);

    }

    @Override
    public UserInfo selectUserByUserName(String username) {
        return userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUsername,username));
    }

    @Override
    public Boolean updateUserLogin(UpdateUserLogin updateUserLogin) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(updateUserLogin.getUserId());
        userInfo.setLastLoginIp(updateUserLogin.getLastLoginIp());
        userInfo.setLastLoginTime(updateUserLogin.getLastLoginTime());
        userInfoMapper.updateById(userInfo);
        return true;
    }

}
