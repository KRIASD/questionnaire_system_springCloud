package com.zhy.questionnairesystem.service;

import com.zhy.domain.User;
import com.zhy.questionnairesystem.hystrix.UserConsumerFeignServiceHystrix;
import com.zhy.utils.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Component
@FeignClient(value = "USER-PROVIDE", fallback = UserConsumerFeignServiceHystrix.class)
public interface UserConsumerFeignService {

    /**
     * 发送手机验证码
     *
     * @param phone 手机号
     * @return 200:发送成功 500:发送失败
     */
    @GetMapping("/user/sendVerificationCode/{phone}")
    CommonResult<?> sendVerificationCode(@PathVariable("phone") String phone);

    /**
     * 用户登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @return 200:登录成功 500:登录失败
     */
    @PostMapping("/user/login/{username}/{password}")
    CommonResult<?> login(@PathVariable("username") String username, @PathVariable("password") String password);

    /**
     * 用户登录方法（使用手机验证码）
     *
     * @param phone            手机号码
     * @param verificationCode 验证码
     * @return 200:登录成功 500:登录失败
     */
    @GetMapping("/user/loginByCode/{phone}/{verificationCode}")
    CommonResult<?> loginByVerificationCode(@PathVariable("phone") String phone, @PathVariable("verificationCode") String verificationCode);

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/user/userInfo/username/{username}")
    CommonResult<?> getUserInfoByUsername(@PathVariable("username") String username);

    /**
     * 根据用户id获取用户信息
     *
     * @param id 用户id
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/user/userInfo/id/{id}")
    CommonResult<?> getUseInfoById(@PathVariable("id") Integer id);

    /**
     * 用户注册方法
     *
     * @param user 用户信息
     * @return 200:注册成功 500:注册失败
     */
    @PutMapping("/user/register")
    CommonResult<?> register(@RequestBody User user);

    /**
     * 用户更新头像
     *
     * @param username 用户名
     * @return 200:更新成功 500:更新失败
     */
    @PutMapping("/user/updateAvatar/{username}")
    CommonResult<?> updateAvatar(@PathVariable("username") String username, @RequestPart("avatar") MultipartFile avatar);

    /**
     * 用户更新信息
     *
     * @param userUpdateInfoFile 用户更新信息文件
     * @return 200:更新成功 500:更新失败
     */
    @PutMapping(value = "/user/updateUserInfo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    CommonResult<?> updateUserInfo(@RequestPart("userUpdateInfo") MultipartFile userUpdateInfoFile);

    /**
     * 修改密码（发送手机验证码）
     *
     * @param user 用户信息
     * @return 200:发送成功且修改成功 500:发送失败或修改失败
     */
    @PutMapping("/user/updatePassword/{verificationCode}")
    CommonResult<?> updatePassword(@RequestBody User user, @PathVariable("verificationCode") String verificationCode);

    /**
     * 退出登录
     *
     * @param username 用户名
     * @return 200:退出成功 500:退出失败
     */
    @PutMapping("/user/logout/{username}")
    CommonResult<?> logout(@PathVariable("username") String username);

    /**
     * 账户彻底注销删除
     *
     * @param username         用户名
     * @param verificationCode 验证码
     * @return 200:注销成功 500:注销失败
     */
    @DeleteMapping("/user/deleteUser/{username}/{verificationCode}")
    CommonResult<?> deleteUser(@PathVariable("username") String username, @PathVariable("verificationCode") String verificationCode);

    /**
     * 获取用户列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/user/userList/{currentPage}/{pageSize}")
    CommonResult<?> getUserList(@PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize);


    /**
     * 测试断路器
     *
     * @param id id
     * @return 200:调用成功 500:调用失败
     */
    @GetMapping("/user/testCircuitBreaker/{id}")
    CommonResult<?> testCircuitBreaker(@PathVariable("id") Integer id);


}
