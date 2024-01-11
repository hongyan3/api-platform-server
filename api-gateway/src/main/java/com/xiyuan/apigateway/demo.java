package com.xiyuan.apigateway;

import com.xiyuan.apicommon.model.entity.User;
import com.xiyuan.apicommon.service.InnerInterfaceInfoService;
import com.xiyuan.apicommon.service.InnerUserService;
import org.apache.dubbo.config.annotation.DubboReference;
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
    private InnerInterfaceInfoService innerInterfaceInfoService;
    @DubboReference
    private InnerUserService userService;

    @GetMapping("/user")
    public User demo2(@RequestParam("userId")Long userId) {
        return userService.getUserById(userId);
    }
}
