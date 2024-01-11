package com.xiyuan.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class InterfaceInvokeFilter implements GlobalFilter {
    private static final List<String> IP_WHITE_LIST = Collections.singletonList("127.0.0.1");
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 1. 请求日志
        log.info("UUID: {},Path: {},Method: {},Params: {},Origin: {}",request.getId(),request.getPath(),request.getMethod(),request.getQueryParams(),request.getRemoteAddress());
        InetSocketAddress localAddress = request.getLocalAddress();
        // 2. 白名单校验
        if (localAddress == null || !IP_WHITE_LIST.contains(localAddress.getHostString())) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        // 3. 用户鉴权（校验AK、SK是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        return chain.filter(exchange);
    }
}
