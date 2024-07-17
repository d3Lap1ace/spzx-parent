package com.spzx.user.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserInfo;

/**
 * 会员Service接口
 *
 * @author lucas
 * @date 2024-07-10
 */
public interface IUserInfoService extends IService<UserInfo>
{

    /**
     * 查询会员列表
     *
     * @param userInfo 会员
     * @return 会员集合
     */
    public List<UserInfo> selectUserInfoList(UserInfo userInfo);

    /**
     * 用户登录
     * @param userInfo
     */
    void register(UserInfo userInfo);

    /**
     * 根据用户名获取用户信息
     * @param username
     * @return
     */
    UserInfo selectUserByUserName(String username);

    /**
     * 跟新用户登录信息
     * @param updateUserLogin
     * @return
     */
    Boolean updateUserLogin(UpdateUserLogin updateUserLogin);
}
