package com.xiyuan.apibackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuan.apibackend.model.dto.user.UserEditRequest;
import com.xiyuan.apibackend.model.dto.user.UserLoginRequest;
import com.xiyuan.apibackend.model.dto.user.UserQueryRequest;
import com.xiyuan.apibackend.model.dto.user.UserRegisterRequest;
import com.xiyuan.apicommon.model.entity.User;
import com.xiyuan.apibackend.model.vo.UserCredentialsVO;
import com.xiyuan.apibackend.model.vo.UserVO;
import com.xiyuan.apibackend.common.BaseResponse;
import com.xiyuan.apibackend.common.ErrorCode;
import com.xiyuan.apibackend.common.ResultUtils;
import com.xiyuan.apibackend.exception.BusinessException;
import com.xiyuan.apibackend.exception.ThrowUtils;
import com.xiyuan.apibackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户接口
 *
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String userName = userRegisterRequest.getUserName();
        // 校验参数是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,userName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userName, userPassword, checkPassword);
        return ResultUtils.success(result);
    }
    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO userVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(userVO);
    }
    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }
    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest
     * @return
     */
    @GetMapping
    public BaseResponse<Page<UserVO>> userList(@ModelAttribute UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    /**
     * 更新个人信息（用户）
     *
     * @param userUpdateMyRequest
     * @param request
     * @return
     */
    @PutMapping
    public BaseResponse<Boolean> updateUser(@RequestBody UserEditRequest userUpdateMyRequest,
                                              HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 刷新Credentials
     *
     * @param request
     */
    @PostMapping("/credentials/refresh")
    public BaseResponse<UserCredentialsVO> refreshCredentials(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        UserCredentialsVO credentialsVO = userService.refreshCredentials(user);
        return ResultUtils.success(credentialsVO);
    }

    /**
     * 获取Credentials
     *
     * @param
     */
    @GetMapping("/credentials")
    public BaseResponse<UserCredentialsVO> getCredentials(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        user = userService.getById(user);
        UserCredentialsVO credentials = new UserCredentialsVO();
        BeanUtils.copyProperties(user,credentials);
        return ResultUtils.success(credentials);
    }
}
