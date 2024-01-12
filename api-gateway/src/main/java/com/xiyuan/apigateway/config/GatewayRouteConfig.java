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
                .route(r -> r.path("/api/**")
                        .filters(f -> f.addRequestHeader("resource","gateway")
                                .filter(filter)
                        )
                        .uri("lb://api-interface")
                )
                .build();
    }
}
