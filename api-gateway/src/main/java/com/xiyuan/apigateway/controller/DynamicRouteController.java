package com.xiyuan.apigateway.controller;

import com.xiyuan.apicommon.common.BaseResponse;
import com.xiyuan.apicommon.common.ResultUtils;
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
@RequestMapping("/gateway")
public class DynamicRouteController {
    @Resource
    private DynamicRouteService dynamicRouteService;

    @GetMapping("/route")
    public BaseResponse<Flux<RouteDefinition>> getRoutes() {
        Flux<RouteDefinition> routeDefinitionFlux = dynamicRouteService.get();
        return ResultUtils.success(routeDefinitionFlux);
    }
    @DeleteMapping("/route/{routeId}")
    public BaseResponse<Boolean> deleteRoute(@PathVariable String routeId) {
        return ResultUtils.success(dynamicRouteService.delete(routeId));
    }
    @PostMapping("/route")
    public BaseResponse<Boolean> addRoute(@RequestBody RouteDefinition definition) {
        return ResultUtils.success(dynamicRouteService.add(definition));
    }
    @PutMapping("/route")
    public BaseResponse<Boolean> updateRoute(@RequestBody RouteDefinition definition) {
        return ResultUtils.success(dynamicRouteService.update(definition));
    }
}
