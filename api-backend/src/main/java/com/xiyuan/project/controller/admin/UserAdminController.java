package com.xiyuan.project.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuan.project.annotation.AuthCheck;
import com.xiyuan.project.common.BaseResponse;
import com.xiyuan.project.common.ErrorCode;
import com.xiyuan.project.common.ResultUtils;
import com.xiyuan.project.exception.BusinessException;
import com.xiyuan.project.exception.ThrowUtils;
import com.xiyuan.project.model.dto.user.UserAddRequest;
import com.xiyuan.project.model.dto.user.UserQueryRequest;
import com.xiyuan.project.model.dto.user.UserUpdateRequest;
import com.xiyuan.project.model.entity.User;
import com.xiyuan.project.model.enums.UserRoleEnum;
import com.xiyuan.project.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author xiyuan
 * @description TODO
 * @date 2024/1/8 15:54
 */
@RestController
@RequestMapping("/admin/user")
public class UserAdminController {
    @Resource
    private UserService userService;
    /**
     * 创建用户（仅管理员）
     *
     * @param userAddRequest
     * @return
     */
    @PostMapping
    @AuthCheck(AccessRole = UserRoleEnum.ADMIN)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        String password = userService.EncryptPassword(userAddRequest.getPassword());
        user.setPassword(password);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }
    /**
     * 删除用户(仅管理员)
     *
     * @param userId
     * @param request
     * @return
     */
    @DeleteMapping("/{userId}")
    @AuthCheck(AccessRole = UserRoleEnum.ADMIN)
    public BaseResponse<Boolean> deleteUser(HttpServletRequest request, @PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isSuccess = userService.removeById(userId);
        return ResultUtils.success(isSuccess);
    }

    /**
     * 更新用户(仅管理员)
     *
     * @param userUpdateRequest
     * @return
     */
    @PutMapping
    @AuthCheck(AccessRole = UserRoleEnum.ADMIN)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @return
     */
    @GetMapping
    @AuthCheck(AccessRole = UserRoleEnum.ADMIN)
    public BaseResponse<Page<User>> userList(@ModelAttribute UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param userId
     * @param request
     * @return
     */
    @GetMapping("/{userId}")
    @AuthCheck(AccessRole = UserRoleEnum.ADMIN)
    public BaseResponse<User> getUserById(@PathVariable Long userId,HttpServletRequest request) {
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }
}
