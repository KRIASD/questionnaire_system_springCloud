package com.zhy.questionnairesystem.service;

import com.aliyuncs.exceptions.ClientException;
import com.zhy.utils.CommonResult;

public interface SmsService {
    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @return 200:发送成功 500:发送失败
     */
    CommonResult<?> sendVerificationCode(String phone) throws ClientException;
}
