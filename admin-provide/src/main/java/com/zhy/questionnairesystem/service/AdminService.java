package com.zhy.questionnairesystem.service;


import com.zhy.utils.CommonResult;

public interface AdminService {
    /**
     * 管理员登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @return 200:登录成功 500:登录失败
     */
    CommonResult<?> login(String username, String password);

    /**
     * 获取管理员信息
     *
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getAdminInfo(String username);
}