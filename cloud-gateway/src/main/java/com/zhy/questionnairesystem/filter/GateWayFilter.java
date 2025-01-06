package com.zhy.questionnairesystem.filter;

import com.fasterxml.jackson.core.filter.TokenFilter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GateWayFilter implements GlobalFilter, Ordered {
    Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //检测当前请求的header中是否有token
        String token = exchange.getRequest().getHeaders().getFirst("token");
        log.info("token:{}", token);
        if (token == null) {
            //如果没有token，直接返回
            logger.info("token为空");
            //放行部分请求
            if (exchange.getRequest().getURI().getPath().contains("/user/login") || exchange.getRequest().getURI().getPath().contains("/user/register") || exchange.getRequest().getURI().getPath().contains("/user/loginByVerificationCode")) {
                return chain.filter(exchange);
            }
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        //如果有token，继续执行
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        //代表加载过滤器的顺序，越小越先执行，因为我们这个过滤器是检测token的，所以要先于其他过滤器执行，所以这里设置为-100，如果不设置，那么默认为0
        return -100;
    }
}
