package com.xiyuan.apigateway;

import com.xiyuan.apigateway.config.RedisDefinitionRepository;
import com.xiyuan.apigateway.service.DynamicRouteService;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

/**
 * @author xiyuan
 * @description TODO
 * @date 2024/3/28 15:07
 */
@RestController
@RequestMapping("/route")
public class TestController {
    @Resource
    private DynamicRouteService dynamicRouteService;

    @GetMapping
    public Flux<RouteDefinition> getRoutes() {
        return dynamicRouteService.get();
    }
    @DeleteMapping("/{routeId}")
    public String deleteRoute(@PathVariable String routeId) {
        return dynamicRouteService.delete(routeId);
    }
    @PostMapping
    public String addRoute(@RequestBody RouteDefinition definition) {
        return dynamicRouteService.add(definition);
    }
    @PutMapping
    public String updateRoute(@RequestBody RouteDefinition definition) {
        return dynamicRouteService.update(definition);
    }
}
