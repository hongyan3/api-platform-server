package com.xiyuan.apigateway;

import com.xiyuan.project.dubbo.ApiBackendService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiyuan
 * @description TODO
 * @date 2024/1/11 21:06
 */
@RestController
public class demo {
    @DubboReference
    private ApiBackendService apiBackendService;

    @GetMapping("/name")
    public String demo1(@RequestParam("name") String name) {
        return apiBackendService.getUserName(name);
    }
}
