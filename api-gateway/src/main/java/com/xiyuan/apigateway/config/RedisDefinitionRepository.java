package com.xiyuan.apigateway.config;

import cn.hutool.json.JSONUtil;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author xiyuan
 * @description 路由存储持久化到Redis
 * @date 2024/3/28 13:59
 */
@Component
public class RedisDefinitionRepository implements RouteDefinitionRepository {
    // hash存储的key
    public static final String GATEWAY_DYNAMIC_ROUTES = "api:gateway:dynamic_routes";
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取路由信息
     * @return
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<Object> values = stringRedisTemplate.opsForHash().values(GATEWAY_DYNAMIC_ROUTES);
        if(!CollectionUtils.isEmpty(values)) {
            List<RouteDefinition> routes = values.stream()
                    .map(e -> JSONUtil.toBean((String) e, RouteDefinition.class))
                    .collect(Collectors.toList());
            return Flux.fromIterable(routes);
        } else {
            return Flux.fromIterable(new ArrayList<>());
        }
    }

    /**
     * 新增路由
     * @param route
     * @return
     */
    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(r -> {
          stringRedisTemplate.opsForHash().put(GATEWAY_DYNAMIC_ROUTES,r.getId(),JSONUtil.toJsonStr(r));
          return Mono.empty();
        });
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> {
            Object route = stringRedisTemplate.opsForHash().get(GATEWAY_DYNAMIC_ROUTES, id);
            if (!Objects.isNull(route)) {
                stringRedisTemplate.opsForHash().delete(GATEWAY_DYNAMIC_ROUTES,id);
                return Mono.empty();
            }
            return Mono.defer(() -> Mono.error(
                    new NotFoundException("RouteDefinition Not Found: " + routeId)
            ));
        });
    }
}
