package com.zhy.questionnairesystem.controller;

import com.zhy.questionnairesystem.service.AdminService;
import com.zhy.utils.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {
    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @return 200:登录成功 500:登录失败
     */
    @RequestMapping("/login/{username}/{password}")
    public CommonResult<?> login(@PathVariable String username, @PathVariable String password) {
        return adminService.login(username, password);
    }

    /**
     * 获取管理员信息
     *
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/info/{username}")
    public CommonResult<?> getAdminInfo(@PathVariable String username) {
        return adminService.getAdminInfo(username);
    }

}