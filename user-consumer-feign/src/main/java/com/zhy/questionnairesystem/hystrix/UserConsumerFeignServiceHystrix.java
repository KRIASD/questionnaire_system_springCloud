package com.zhy.questionnairesystem.hystrix;

import cn.hutool.core.util.IdUtil;
import com.zhy.domain.User;
import com.zhy.questionnairesystem.service.UserConsumerFeignService;
import com.zhy.utils.CommonResult;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UserConsumerFeignServiceHystrix implements UserConsumerFeignService {

    /**
     * 发送手机验证码
     *
     * @param phone 手机号
     * @return 200:发送成功 500:发送失败
     */
    @Override
    public CommonResult<?> sendVerificationCode(String phone) {
        return CommonResult.failed("发送失败");
    }

    /**
     * 用户登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @return 200:登录成功 500:登录失败
     */
    @Override
    public CommonResult<?> login(String username, String password) {
        return CommonResult.failed("登录失败");
    }

    /**
     * 用户登录方法（使用手机验证码）
     *
     * @param phone            手机号码
     * @param verificationCode 验证码
     * @return 200:登录成功 500:登录失败
     */
    @Override
    public CommonResult<?> loginByVerificationCode(String phone, String verificationCode) {
        return CommonResult.failed("登录失败");
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getUserInfoByUsername(String username) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 根据用户id获取用户信息
     *
     * @param id 用户id
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getUseInfoById(Integer id) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 用户注册方法
     *
     * @param user             用户信息
     * @return 200:注册成功 500:注册失败
     */
    @Override
    public CommonResult<?> register(User user) {
        return CommonResult.failed("注册失败");
    }

    /**
     * 用户更新头像
     *
     * @param username 用户名
     * @param avatar   头像文件
     * @return 200:更新成功 500:更新失败
     */
    @Override
    public CommonResult<?> updateAvatar(String username, MultipartFile avatar) {
        return CommonResult.failed("更新失败");
    }

    /**
     * 用户更新信息
     *
     * @param userUpdateInfoFile 用户更新信息文件
     * @return 200:更新成功 500:更新失败
     */
    @Override
    public CommonResult<?> updateUserInfo(MultipartFile userUpdateInfoFile) {
        return CommonResult.failed("更新失败");
    }

    /**
     * 修改密码（发送手机验证码）
     *
     * @param user             用户信息
     * @param verificationCode 验证码
     * @return 200:发送成功且修改成功 500:发送失败或修改失败
     */
    @Override
    public CommonResult<?> updatePassword(User user, String verificationCode) {
        return CommonResult.failed("修改失败");
    }

    /**
     * 退出登录
     *
     * @param username 用户名
     * @return 200:退出成功 500:退出失败
     */
    @Override
    public CommonResult<?> logout(String username) {
        return CommonResult.failed("退出失败");
    }

    /**
     * 账户彻底注销删除
     *
     * @param username         用户名
     * @param verificationCode 验证码
     * @return 200:注销成功 500:注销失败
     */
    @Override
    public CommonResult<?> deleteUser(String username, String verificationCode) {
        return CommonResult.failed("注销失败");
    }

    /**
     * 获取用户列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页大小
     * @return 用户列表
     */
    @Override
    public CommonResult<?> getUserList(Integer currentPage, Integer pageSize) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 测试断路器
     *
     * @param id id
     * @return 200:调用成功 500:调用失败
     */

    @Override
    public CommonResult<?> testCircuitBreaker(Integer id) {
        if (id < 0) {
            throw new RuntimeException("id不能为负数");
        }
        String serialNumber = IdUtil.simpleUUID();
        return CommonResult.success(serialNumber);
    }
}
