package com.xiyuan.apicore.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuan.apicore.annotation.AuthCheck;
import com.xiyuan.apicore.common.BaseResponse;
import com.xiyuan.apicore.common.ErrorCode;
import com.xiyuan.apicore.common.ResultUtils;
import com.xiyuan.apicore.exception.BusinessException;
import com.xiyuan.apicore.exception.ThrowUtils;
import com.xiyuan.apicore.model.dto.userinterfaceinfo.UserInterfaceInfoAddRequest;
import com.xiyuan.apicore.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.xiyuan.apicore.model.dto.userinterfaceinfo.UserInterfaceInfoUpdateRequest;
import com.xiyuan.apicore.model.entity.UserInterfaceInfo;
import com.xiyuan.apicore.model.enums.UserRoleEnum;
import com.xiyuan.apicore.model.vo.UserInterfaceInfoVO;
import com.xiyuan.apicore.service.UserInterfaceInfoService;
import com.xiyuan.apicore.service.UserService;
import com.xiyuan.apicommon.model.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author xiyuan
 * @description TODO
 * @date 2024/1/8 16:32
 */
@RestController
@RequestMapping("/admin/user_interface_info")
public class UserInterfaceInfoAdminController {
    @Resource
    private UserInterfaceInfoService userUserInterfaceInfoService;

    @Resource
    private UserService userService;

    /**
     * 新增接口(仅管理员)
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping
    @AuthCheck(AccessRole = UserRoleEnum.ADMIN)
    public BaseResponse<Long> addUserInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo interfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        userUserInterfaceInfoService.validUserInterfaceInfo(interfaceInfo,true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = userUserInterfaceInfoService.save(interfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newUserInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newUserInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param interfaceId
     * @param request
     * @return
     */
    @DeleteMapping("/{interfaceId}")
    @AuthCheck(AccessRole = UserRoleEnum.ADMIN)
    public BaseResponse<Boolean> deleteUserInterfaceInfo(HttpServletRequest request, @PathVariable Long interfaceId) {
        if (interfaceId == null || interfaceId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = interfaceId;
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userUserInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = userUserInterfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param interfaceInfoUpdateRequest
     * @return
     */
    @PutMapping
    @AuthCheck(AccessRole = UserRoleEnum.ADMIN)
    public BaseResponse<Boolean> updateUserInterfaceInfo(@RequestBody UserInterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo interfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        userUserInterfaceInfoService.validUserInterfaceInfo(interfaceInfo,false);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userUserInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = userUserInterfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }
    /**
     * 分页获取列表（封装类）
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping
    @AuthCheck(AccessRole = UserRoleEnum.ADMIN)
    public BaseResponse<Page<UserInterfaceInfoVO>> getUserInterfaceInfoList(@ModelAttribute UserInterfaceInfoQueryRequest interfaceInfoQueryRequest,
                                                                            HttpServletRequest request) {
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 100, ErrorCode.PARAMS_ERROR);
        Page<UserInterfaceInfo> interfaceInfoPage = userUserInterfaceInfoService.page(new Page<>(current, size),
                userUserInterfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
        return ResultUtils.success(userUserInterfaceInfoService.getUserInterfaceInfoVOPage(interfaceInfoPage));
    }
}
