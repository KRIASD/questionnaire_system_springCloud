package com.zhy.questionnairesystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhy.domain.Admin;
import com.zhy.questionnairesystem.mapper.AdminMapper;
import com.zhy.questionnairesystem.service.AdminService;
import com.zhy.questionnairesystem.service.SmsService;
import com.zhy.utils.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private SmsService smsService;
    @Autowired
    private AdminMapper adminMapper;

    /**
     * 管理员登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @return 200:登录成功 500:登录失败
     */
    @Override
    public CommonResult<?> login(String username, String password) {
        String cacheAdminString = redisTemplate.opsForValue().get("admin:" + username);
        Admin cacheAdmin = JSON.parseObject(cacheAdminString, Admin.class);
        if (cacheAdminString == null) {
            Admin admin = new Admin();
            admin.setUserName(username);
            admin.setPassWord(password);
            QueryWrapper<Admin> wrapper = new QueryWrapper<>(admin);
            wrapper.eq("username", username);
            wrapper.eq("password", password);
            Admin dbAdmin = adminMapper.selectOne(wrapper);
            System.out.println("从数据库中查询");
            System.out.println(dbAdmin);
            if (dbAdmin != null) {
                redisTemplate.opsForValue().set("admin:" + username, JSON.toJSONString(dbAdmin));
                return CommonResult.success(dbAdmin);
            }
            return CommonResult.failed("用户名或密码错误");
        }
        System.out.println("从缓存中查询");
        System.out.println(cacheAdmin);
        if (cacheAdmin.getPassWord().equals(password)) {
            return CommonResult.success(cacheAdmin);
        }
        return CommonResult.failed("用户名或密码错误");
    }

    /**
     * 获取管理员信息
     *
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getAdminInfo(String username) {
        String cacheAdminString = redisTemplate.opsForValue().get("admin:" + username);
        Admin cacheAdmin = JSON.parseObject(cacheAdminString, Admin.class);
        if (cacheAdminString == null) {
            Admin admin = new Admin();
            admin.setUserName(username);
            QueryWrapper<Admin> wrapper = new QueryWrapper<>(admin);
            wrapper.eq("username", username);
            Admin dbAdmin = adminMapper.selectOne(wrapper);
            System.out.println("从数据库中查询");
            System.out.println(dbAdmin);
            if (dbAdmin != null) {
                redisTemplate.opsForValue().set("admin:" + username, JSON.toJSONString(dbAdmin));
                return CommonResult.success(dbAdmin);
            }
            return CommonResult.failed("获取失败");
        }
        System.out.println("从缓存中查询");
        System.out.println(cacheAdmin);
        return CommonResult.success(cacheAdmin);
    }
}

