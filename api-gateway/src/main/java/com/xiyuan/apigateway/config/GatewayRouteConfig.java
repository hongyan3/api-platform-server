package com.xiyuan.apigateway.config;

import com.xiyuan.apigateway.filter.InterfaceInvokeFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GatewayRouteConfig {
    /**
     * 对调用interface服务的请求加上resource请求头
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator invokeInterface(RouteLocatorBuilder builder,InterfaceInvokeFilter filter) {
        return builder.routes()
                .route(r -> r.path("/api_interface/**")
                        .filters(f -> f.addRequestHeader("resource","gateway")
                                //重写请求路径
                                .rewritePath("/api_interface/(?<segment>.*)","/$\\{segment}")
                                .filter(filter)
                        )
                        .uri("http://127.0.0.1:8081")
                )
                .build();
    }
}
