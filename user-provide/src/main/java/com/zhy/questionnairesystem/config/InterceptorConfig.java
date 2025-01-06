package com.zhy.questionnairesystem.config;

import com.zhy.interceptor.JWTInterceptors;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器
        registry.addInterceptor(new JWTInterceptors())
                .addPathPatterns("/user/**")  // 拦截所有/user/**请求
                .excludePathPatterns("/user/login/**");  // 放行登录请求
    }
}
