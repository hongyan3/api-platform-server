package com.xiyuan.apigateway.filter;

import com.xiyuan.apiclientsdk.utils.SignatureUtils;
import com.xiyuan.apicommon.exception.BusinessException;
import com.xiyuan.apicommon.model.entity.User;
import com.xiyuan.apicommon.model.enums.ErrorCode;
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
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Slf4j
@Component
public class BizServerFilter implements GatewayFilter, Ordered {
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // 3. 用户鉴权（校验AK、SK是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("access_key");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        if (StringUtils.isAllBlank(accessKey,nonce,timestamp,sign,body)) {
            log.error("缺少请求参数");
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User invokeUser = null;
        try {
            invokeUser = innerInterfaceInfoService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("远程调用获取调用用户信息失败: {}",e.getMessage());
        }
        if (invokeUser == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR);
        }
        // 3.1 时间戳校验
        long now = System.currentTimeMillis()/1000;
        if (timestamp == null || now-Long.parseLong(timestamp) > 30) {
            log.error("时间校验失败");
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 3.2 签名校验
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignatureUtils.generateSignature(body, secretKey);
        if (sign == null || !sign.equals(serverSign)) {
            log.error("签名校验失败");
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 3.3 随机数校验
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(CommonConstant.NONCE_REDIS_PREFIX, nonce);
        if (Boolean.TRUE.equals(isMember)) {
            log.error("重复请求");
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR);
        } else {
            stringRedisTemplate.opsForSet().add(CommonConstant.NONCE_REDIS_PREFIX,nonce);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
