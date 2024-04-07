package com.xiyuan.apigateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiyuan.apicommon.common.BaseResponse;
import com.xiyuan.apicommon.common.ResultUtils;
import com.xiyuan.apicommon.exception.BusinessException;
import com.xiyuan.apicommon.model.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author xiyuan
 * @description TODO
 * @date 2024/3/28 17:11
 */
@Configuration
@Slf4j
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        BaseResponse<?> result;
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        //3.设置响应状态码（在这里捕获我们自定义的异常）
        if (ex instanceof BusinessException) {
            response.setStatusCode(HttpStatus.OK);
            result = ResultUtils.error(((BusinessException) ex).getCode(),ex.getMessage());
        } else if(ex instanceof NotFoundException) {
            result = ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        } else {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            result = ResultUtils.error(ErrorCode.SYSTEM_ERROR,ex.getMessage());
        }
        log.error("[{}] {}: {}",request.getHeaders().get("traceId"),ex,ex.getStackTrace());
        try {
            byte[] errBytes = MAPPER.writeValueAsBytes(result);
            DataBuffer wrap = response.bufferFactory().wrap(errBytes);
            return response.writeWith(Mono.just(wrap));
        } catch (JsonProcessingException e) {
            log.error("JSON序列化异常：{}", e.getMessage());
            return Mono.error(ex);
        }
    }
}
