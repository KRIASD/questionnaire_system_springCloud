package com.zhy.questionnairesystem.hystrix;

import com.zhy.questionnairesystem.service.AdminConsumerFeignService;
import com.zhy.utils.CommonResult;
import org.springframework.stereotype.Component;

@Component
public class AdminConsumerFeignServiceHystrix implements AdminConsumerFeignService {

    /**
     * 管理员登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @return 200:登录成功 500:登录失败
     */
    @Override
    public CommonResult<?> login(String username, String password) {
        return null;
    }

    /**
     * 获取管理员信息
     *
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getAdminInfo(String username) {
        return null;
    }
}
