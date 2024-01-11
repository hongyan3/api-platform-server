package com.xiyuan.project.provider;

import com.xiyuan.project.dubbo.ApiBackendService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author xiyuan
 * @description TODO
 * @date 2024/1/11 21:03
 */
@DubboService
public class ApiBackendServiceImpl implements ApiBackendService {
    public String getUserName(String name) {
        return "您的用户名为："+name;
    }
}
