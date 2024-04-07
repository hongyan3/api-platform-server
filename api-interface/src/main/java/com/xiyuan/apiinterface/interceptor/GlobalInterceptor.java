package com.xiyuan.apiinterface.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class GlobalInterceptor implements HandlerInterceptor {
    private static final String TRACE_ID="traceId";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String resource = request.getHeader("resource");
        String traceId = request.getHeader(TRACE_ID);
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String origin = request.getRemoteAddr();
        log.info("[{}] uuid: {}, url: {}, origin: {}",method,traceId,url,origin);
        if (resource == null || !resource.equals("gateway")) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }
        return true;
    }
}
