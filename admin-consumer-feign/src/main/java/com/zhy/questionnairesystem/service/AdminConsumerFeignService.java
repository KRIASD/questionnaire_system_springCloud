package com.zhy.questionnairesystem.service;

import com.zhy.questionnairesystem.hystrix.AdminConsumerFeignServiceHystrix;
import com.zhy.utils.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "ADMIN-PROVIDE", fallback = AdminConsumerFeignServiceHystrix.class)
public interface AdminConsumerFeignService {
    @GetMapping("/admin/login/{username}/{password}")
    CommonResult<?> login(@PathVariable("username") String username, @PathVariable("password") String password);

    /**
     * 获取管理员信息
     *
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/admin/info/{username}")
    CommonResult<?> getAdminInfo(@PathVariable("username") String username);


}
