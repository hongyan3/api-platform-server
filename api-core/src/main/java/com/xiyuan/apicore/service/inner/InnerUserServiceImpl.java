package com.xiyuan.apicore.service.inner;

import com.xiyuan.apicore.service.UserService;
import com.xiyuan.apicommon.model.entity.User;
import com.xiyuan.apicommon.service.InnerUserService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author xiyuan
 * @description TODO
 * @date 2024/1/11 22:27
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {
    @Resource
    private UserService userService;
    @Override
    public User getUserById(Long userId) {
        return userService.getById(userId);
    }
}
