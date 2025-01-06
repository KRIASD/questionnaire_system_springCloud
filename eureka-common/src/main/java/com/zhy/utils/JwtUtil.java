package com.zhy.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;


public class JwtUtil {
    private static final String SING = "QUESTIONNAIREMASTER";


    public static String getToken(Map<String, String> map) {
        //过期时间设置为半个小时
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, 30);
        //创建jwt builder
        JWTCreator.Builder builder = JWT.create();
        // payload
        map.forEach((k, v) -> {
            builder.withClaim(k, v);
        });
        String token = builder.withExpiresAt(instance.getTime())  //指定令牌过期时间
                .sign(Algorithm.HMAC256(SING));  // sign
        return token;
    }

    /**
     * 验证token  合法性
     */
    public static DecodedJWT verify(String token) {
        return JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
    }
}