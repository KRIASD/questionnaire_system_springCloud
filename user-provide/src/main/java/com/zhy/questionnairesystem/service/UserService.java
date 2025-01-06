package com.zhy.questionnairesystem.service;

import com.aliyuncs.exceptions.ClientException;
import com.zhy.domain.User;
import com.zhy.utils.CommonResult;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    /**
     * 用户登录方法
     *
     * @param userName 用户名
     * @param passWord 密码
     * @return 200:登录成功 500:登录失败
     */
    CommonResult<?> login(String userName, String passWord);

    /**
     * 用户登录方法（使用手机验证码）
     *
     * @param phone            手机号码
     * @param verificationCode 验证码
     * @return 200:登录成功 500:登录失败
     */
    CommonResult<?> loginByVerificationCode(String phone, String verificationCode) throws ClientException;

    /**
     * 用户注册方法
     *
     * @param user             用户信息
     * @return 200:注册成功 500:注册失败
     */
    CommonResult<?> register(User user);

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */

    CommonResult<?> getUserByUsername(String username);

    /**
     * 根据用户id获取用户信息
     *
     * @param id 用户id
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getUserById(Integer id);

    /**
     * 用户更新头像
     *
     * @param username 用户名
     * @param avatar   头像文件
     * @return 1:更新成功 0:更新失败
     */
    CommonResult<?> updateAvatar(String username, MultipartFile avatar);

    /**
     * 用户更新信息
     *
     * @param user   用户信息
     * @param avatar 头像文件
     * @return 200:更新成功 500:更新失败
     */
    CommonResult<?> updateUserInfo(User user, MultipartFile avatar);

    /**
     * 用户更新密码（发送验证码）
     *
     * @param verificationCode 验证码
     * @param user             用户信息
     * @return 200:发送成功且修改成功 500:发送失败或修改失败
     */
    CommonResult<?> updatePassword(String verificationCode, User user) throws ClientException;

    /**
     * 发送手机验证码
     *
     * @param phone 手机号
     * @return 200:发送成功 500:发送失败
     */
    CommonResult<?> sendVerificationCode(String phone);

    /**
     * 退出登录
     *
     * @param username 用户名
     * @return 200:注销成功 500:注销失败
     */
    CommonResult<?> logout(String username);

    /**
     * 账户彻底注销删除
     *
     * @param username         用户名
     * @param verificationCode 验证码
     * @return 200:注销成功 500:注销失败
     */
    CommonResult<?> deleteUser(String username, String verificationCode);

    /**
     * 获取用户列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页大小
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getUserList(Integer currentPage, Integer pageSize);

    /**
     * 测试断路器
     *
     * @param id id
     * @return 200:调用成功 500:调用失败
     */
    CommonResult<?> testCircuitBreaker(Integer id);

    /**
     * 按照用户登录的时间段按星期统计所有用户的活跃度
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getActiveUserByWeek();

    /**
     * 按照用户提交问卷的时间段，每天刷一次，统计所有用户的活跃度，每四个时间段为一组
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getActiveUserByDay();
}
