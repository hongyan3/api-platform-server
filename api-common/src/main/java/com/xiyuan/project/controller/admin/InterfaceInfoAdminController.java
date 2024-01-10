package com.xiyuan.project.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuan.project.annotation.AuthCheck;
import com.xiyuan.project.common.BaseResponse;
import com.xiyuan.project.common.ErrorCode;
import com.xiyuan.project.common.ResultUtils;
import com.xiyuan.project.exception.BusinessException;
import com.xiyuan.project.exception.ThrowUtils;
import com.xiyuan.project.model.dto.intrefaceinfo.InterfaceInfoAddRequest;
import com.xiyuan.project.model.dto.intrefaceinfo.InterfaceInfoInvokeRequest;
import com.xiyuan.project.model.dto.intrefaceinfo.InterfaceInfoQueryRequest;
import com.xiyuan.project.model.dto.intrefaceinfo.InterfaceInfoUpdateRequest;
import com.xiyuan.project.model.entity.InterfaceInfo;
import com.xiyuan.project.model.entity.User;
import com.xiyuan.project.model.enums.InterfaceInfoStatusEnum;
import com.xiyuan.project.model.enums.UserRoleEnum;
import com.xiyuan.project.model.vo.InterfaceInfoVO;
import com.xiyuan.project.service.InterfaceInfoService;
import com.xiyuan.project.service.UserService;
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
@RequestMapping("/admin/interface_info")
public class InterfaceInfoAdminController {
    @Resource
    private InterfaceInfoService interfaceInfoService;

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
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        interfaceInfoService.validInterfaceInfo(interfaceInfo,true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
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
    public BaseResponse<Boolean> deleteInterfaceInfo(HttpServletRequest request, @PathVariable Long interfaceId) {
        if (interfaceId == null || interfaceId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = interfaceId;
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
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
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo,false);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = interfaceInfoService.updateById(interfaceInfo);
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
    public BaseResponse<Page<InterfaceInfoVO>> getInterfaceInfoList(@ModelAttribute InterfaceInfoQueryRequest interfaceInfoQueryRequest,
                                                                         HttpServletRequest request) {
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 100, ErrorCode.PARAMS_ERROR);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage));
    }
    /**
     * 接口上线
     *
     * @param interfaceId
     * @return
     */
    @PutMapping("/{interfaceId}/online")
    @AuthCheck(AccessRole = UserRoleEnum.ADMIN)
    public BaseResponse<Boolean> onlineInterfaceInfo(@PathVariable Long interfaceId) {
        if (interfaceId == null || interfaceId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(interfaceId);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(interfaceId);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        // todo 判断接口是否可以跑通
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }
    /**
     * 接口下线
     *
     * @param interfaceId
     * @return
     */
    @PutMapping("/{interfaceId}/offline")
    @AuthCheck(AccessRole = UserRoleEnum.ADMIN)
    public BaseResponse<Boolean> offlineInterfaceInfo(@PathVariable Long interfaceId) {
        if (interfaceId == null || interfaceId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(interfaceId);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(interfaceId);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }
}
