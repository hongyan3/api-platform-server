package com.xiyuan.apigateway.filter;

import com.xiyuan.apiclientsdk.utils.SignatureUtils;
import com.xiyuan.apicommon.model.entity.User;
import com.xiyuan.apicommon.service.InnerInterfaceInfoService;
import com.xiyuan.apigateway.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Slf4j
@Component
public class InterfaceInvokeFilter implements GatewayFilter, Ordered {
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 1. 请求日志
        log.info("UUID: {}, Path: {}, Method: {}, Params: {}, Origin: {}",request.getId(),CommonConstant.INTERFACE_HOST+request.getPath(),request.getMethod(),request.getQueryParams(),request.getRemoteAddress());
        InetSocketAddress localAddress = request.getLocalAddress();
        // 2. 白名单校验
        if (localAddress == null || !CommonConstant.IP_WHITE_LIST.contains(localAddress.getHostString())) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        // 3. 用户鉴权（校验AK、SK是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("access_key");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        if (StringUtils.isAllBlank(accessKey,nonce,timestamp,sign,body)) {
            log.error("缺少请求参数");
            return handleNoAuth(response);
        }
        User invokeUser = null;
        try {
            invokeUser = innerInterfaceInfoService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("远程调用获取调用用户信息失败: {}",e.getMessage());
        }
        if (invokeUser == null) {
            return handleNoAuth(response);
        }
        // 3.1 时间戳校验
        long now = System.currentTimeMillis()/1000;
        if (timestamp == null || now-Long.parseLong(timestamp) > 30) {
            log.error("时间校验失败");
            return handleNoAuth(response);
        }
        // 3.2 随机数校验
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignatureUtils.generateSignature(body, secretKey);
        if (sign == null || !sign.equals(serverSign)) {
            log.error("签名校验失败");
            return handleNoAuth(response);
        }
        return chain.filter(exchange);
    }

    /**
     * 处理无权限调用异常
     * @param response
     * @return
     */
    private Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
