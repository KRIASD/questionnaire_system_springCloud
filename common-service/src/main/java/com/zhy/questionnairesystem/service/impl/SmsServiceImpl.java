package com.zhy.questionnairesystem.service.impl;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.zhy.questionnairesystem.service.SmsService;
import com.zhy.utils.CodeUtil;
import com.zhy.utils.CommonResult;
import com.zhy.utils.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @return 200:发送成功 500:发送失败
     */
    @Override
    public CommonResult<?> sendVerificationCode(String phone) throws ClientException {
        String code = CodeUtil.generateVerifyCode(6);
        //将验证码存入redis中
        redisTemplate.opsForValue().set("verificationCode:" + phone, code);
        //设置过期时间
        redisTemplate.expire("verificationCode:" + phone, 60, java.util.concurrent.TimeUnit.SECONDS);
        System.out.println("code = " + code);
        String TemplateParam = "{\"code\":\"" + code + "\"}";
        // 短信模板id
        String TemplateCode = "SMS_464535260";
        SendSmsResponse response = SmsUtil.sendSms(phone, TemplateParam, TemplateCode);
        System.out.println("短信接口返回的数据----------------");
        System.out.println("Code=" + response.getCode());
        System.out.println("Message=" + response.getMessage());
        System.out.println("RequestId=" + response.getRequestId());
        System.out.println("BizId=" + response.getBizId());
        if (response.getCode().equals("OK")) {
            return CommonResult.success("发送成功");
        } else {
            return CommonResult.failed("发送失败");
        }
    }

}
