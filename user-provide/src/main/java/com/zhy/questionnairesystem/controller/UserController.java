package com.zhy.questionnairesystem.controller;

import com.aliyuncs.exceptions.ClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhy.domain.User;
import com.zhy.questionnairesystem.service.UserService;
import com.zhy.resp.UserWithTokenResp;
import com.zhy.utils.CommonResult;
import com.zhy.utils.JwtUtil;
import com.zhy.vo.UserUpdateInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", maxAge = 3600)

public class UserController {
    //注入UserService
    @Autowired
    private UserService userService;

    /**
     * 用户登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @return 200:登录成功 500:登录失败
     */
    @PostMapping("/login/{username}/{password}")
    public CommonResult<?> login(@PathVariable String username, @PathVariable String password) {
        return userService.login(username, password);
    }

    /**
     * 用户登录方法（使用手机验证码）
     *
     * @param phone            手机号码
     * @param verificationCode 验证码
     * @return 200:登录成功 500:登录失败
     */
    @GetMapping("/login/{phone}/{verificationCode}")
    public CommonResult<?> loginByVerificationCode(@PathVariable String phone, @PathVariable String verificationCode) throws ClientException {
        return userService.loginByVerificationCode(phone, verificationCode);
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */

    @GetMapping("/userInfo/username/{username}")
    public CommonResult<?> getUserInfoByUsername(@PathVariable String username) {
        System.out.println(username);
        return userService.getUserByUsername(username);
    }

    /**
     * 根据用户id获取用户信息
     *
     * @param id 用户id
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/userInfo/id/{id}")
    public CommonResult<?> getUseInfoById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    /**
     * 用户注册方法
     *
     * @param user 用户信息
     * @return 200:注册成功 500:注册失败
     */
    @PutMapping("/register")
    public CommonResult<?> register(@RequestBody User user) {
        return userService.register(user);
    }

    /**
     * 用户更新头像
     *
     * @param username 用户名
     * @param avatar   头像
     * @return 200:更新成功 500:更新失败
     */
    @PutMapping("/updateAvatar/{username}")
    public CommonResult<?> updateAvatar(@PathVariable String username, @RequestPart("avatar") MultipartFile avatar) {
        return userService.updateAvatar(username, avatar);
    }

    /**
     * 用户更新信息
     *
     * @param userUpdateInfoFile 用户更新信息文件
     * @return 200:更新成功 500:更新失败
     */
    @PutMapping("/updateUserInfo")
    public CommonResult<?> updateUserInfo(@RequestPart("userUpdateInfo") MultipartFile userUpdateInfoFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserUpdateInfo userUpdateInfo = objectMapper.readValue(userUpdateInfoFile.getBytes(), UserUpdateInfo.class);
        return userService.updateUserInfo(userUpdateInfo.getUser(), userUpdateInfo.getAvatar());
    }

    /**
     * 修改密码（发送手机验证码）
     *
     * @param User 用户信息
     * @return 200:发送成功且修改成功 500:发送失败或修改失败
     */
    @PutMapping("/updatePassword/{verificationCode}")
    public CommonResult<?> updatePassword(@PathVariable String verificationCode, @RequestBody User User) throws ClientException {
        return userService.updatePassword(verificationCode, User);
    }

    /**
     * 发送手机验证码
     *
     * @param phone 手机号
     * @return 200:发送成功 500:发送失败
     */
    @GetMapping("/sendVerificationCode/{phone}")
    public CommonResult<?> sendVerificationCode(@PathVariable String phone) {
        return userService.sendVerificationCode(phone);
    }

    /**
     * 退出登录
     *
     * @param username 用户名
     * @return 200:退出成功 500:退出失败
     */
    @PutMapping("/logout/{username}")
    public CommonResult<?> logout(@PathVariable String username) {
        return userService.logout(username);
    }

    /**
     * 账户彻底注销删除
     *
     * @param username         用户名
     * @param verificationCode 验证码
     * @return 200:注销成功 500:注销失败
     */
    @DeleteMapping("/deleteUser/{username}/{verificationCode}")
    public CommonResult<?> deleteUser(@PathVariable String username, @PathVariable String verificationCode) {
        return userService.deleteUser(username, verificationCode);
    }

    /**
     * 获取用户列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/userList/{currentPage}/{pageSize}")
    public CommonResult<?> getUserList(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        return userService.getUserList(currentPage, pageSize);
    }

    /**
     * 测试断路器
     *
     * @param id id
     * @return 200:调用成功 500:调用失败
     */
    @GetMapping("/testCircuitBreaker/{id}")
    public CommonResult<?> testCircuitBreaker(@PathVariable Integer id) {
        return userService.testCircuitBreaker(id);
    }

    /**
     * 按照用户登录的时间段按星期统计所有用户的活跃度
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/getActiveUserByWeek")
    public CommonResult<?> getActiveUserByWeek() {
        return userService.getActiveUserByWeek();
    }

    /**
     * 按照用户提交问卷的时间段，每天刷一次，统计所有用户的活跃度，每四个时间段为一组
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/getActiveUserByDay")
    public CommonResult<?> getActiveUserByDay() {
        return userService.getActiveUserByDay();
    }

}
