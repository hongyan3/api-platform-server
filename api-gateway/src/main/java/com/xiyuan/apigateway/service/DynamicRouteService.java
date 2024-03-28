package com.xiyuan.apigateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author xiyuan
 * @description TODO
 * @date 2024/3/28 15:12
 */
@Service
@Slf4j
public class DynamicRouteService implements ApplicationEventPublisherAware {
    @Resource
    private RouteDefinitionRepository routeDefinitionWriter;
    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    private void notifyChanged() {
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 获取路由列表
     */
    public Flux<RouteDefinition> get() {
        return routeDefinitionWriter.getRouteDefinitions();
    }

    /**
     * 增加路由
     */
    public String add(RouteDefinition definition) {
        try {
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            notifyChanged();
            return "success";
        } catch (Exception e) {
            log.error("Add Route Failed：{}", e.toString());
            return "Add Route Failed：" + e;
        }
    }

    /**
     * 更新路由
     */
    public String update(RouteDefinition definition) {
        try {
            routeDefinitionWriter.delete(Mono.just(definition.getId())).subscribe();
        } catch (Exception e) {
            return "Update Route Failed,Not Fount Route: " + definition.getId();
        }
        try {
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            notifyChanged();
            return "success";
        } catch (Exception e) {
            log.error("Add Route Failed：{}", e.toString());
            return "Add Route Failed：" + e;
        }
    }
    public String delete(String id) {
        try {
            routeDefinitionWriter.delete(Mono.just(id)).subscribe();
            return "success";
        } catch (Exception e) {
            return "Delete Route Failed: " + id;
        }
    }
}
