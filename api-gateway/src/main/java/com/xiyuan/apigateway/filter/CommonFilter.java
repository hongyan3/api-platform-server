package com.xiyuan.apigateway.filter;

import com.xiyuan.apicommon.exception.BusinessException;
import com.xiyuan.apicommon.model.enums.ErrorCode;
import com.xiyuan.apigateway.service.IpBlackListService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author xiyuan
 * @description 全局拦截器
 * @date 2024/3/29 12:54
 */
@Component
@Slf4j
@Order(0)
public class CommonFilter implements GlobalFilter {
    private static final String TRACE_ID = "traceID";
    @Resource
    private IpBlackListService ipService;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        InetSocketAddress localAddress = request.getLocalAddress();
        List<String> ipBlackList = ipService.getIpBlackList();
        // IP黑名单校验
        if (localAddress == null || ipBlackList.contains(localAddress.getHostString())) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return Mono.error(new BusinessException(ErrorCode.FORBIDDEN_ERROR));
        }
        // 对请求对象的Request进行增强，植入traceId
        String traceId = UUID.randomUUID().toString();
        request.mutate().headers(r -> {
            MDC.put(TRACE_ID,traceId);
            r.set(TRACE_ID,traceId);
            RpcContext.getClientAttachment().setAttachment(TRACE_ID,traceId);
        }).build();
        exchange.mutate().request(request);
        String url = request.getURI().toString();
        String path = request.getPath().toString();
        String method = Objects.requireNonNull(request.getMethod()).toString();
        String params = request.getQueryParams().values().toString();
        String origin = Objects.requireNonNull(request.getRemoteAddress()).toString();
        log.info("[{}] uuid: {}, url: {}, path: {}, params: {}, origin: {}",method,traceId,url,path,params,origin);
        // 跨域放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            response.setStatusCode(HttpStatus.OK);
            return Mono.empty();
        }
        return chain.filter(exchange);
    }
}
