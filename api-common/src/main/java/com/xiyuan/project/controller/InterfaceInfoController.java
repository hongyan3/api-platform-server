package com.xiyuan.project.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuan.project.client.ApiClient;
import com.xiyuan.project.common.BaseResponse;
import com.xiyuan.project.common.ErrorCode;
import com.xiyuan.project.common.ResultUtils;
import com.xiyuan.project.exception.BusinessException;
import com.xiyuan.project.exception.ThrowUtils;
import com.xiyuan.project.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.xiyuan.project.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.xiyuan.project.model.entity.InterfaceInfo;
import com.xiyuan.project.model.entity.User;
import com.xiyuan.project.model.enums.InterfaceInfoStatusEnum;
import com.xiyuan.project.model.vo.InterfaceInfoVO;
import com.xiyuan.project.service.InterfaceInfoService;
import com.xiyuan.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("interface_info")
@Slf4j
public class InterfaceInfoController {
    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    /**
     * 根据 id 获取
     *
     * @param interfaceId
     * @return
     */
    @GetMapping("/{interfaceId}")
    public BaseResponse<InterfaceInfoVO> getInterfaceInfoById(@PathVariable Long interfaceId, HttpServletRequest request) {
        if (interfaceId == null || interfaceId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVO(interfaceInfo));
    }

    /**
     * 分页获取interface列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping
    public BaseResponse<Page<InterfaceInfoVO>> getInterfaceInfoList(@ModelAttribute InterfaceInfoQueryRequest interfaceInfoQueryRequest,
                                                                           HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        interfaceInfoQueryRequest.setUserId(loginUser.getId());
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 100, ErrorCode.PARAMS_ERROR);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage));
    }

    /**
     * 调用接口
     *
     * @param invokeRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<String> invokeInterface(@RequestBody InterfaceInfoInvokeRequest invokeRequest, HttpServletRequest request) {
        if (invokeRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        long id = invokeRequest.getId();
        String userRequestParams = invokeRequest.getRequestParams();
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(interfaceInfo==null,ErrorCode.PARAMS_ERROR);
        if (interfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口未启用");
        }
        User user = userService.getLoginUser(request);
        String accessKey = user.getAccessKey();
        String secretKey = user.getSecretKey();
        ApiClient client = new ApiClient(accessKey,secretKey);
        com.xiyuan.project.model.User paramUser = JSONUtil.toBean(userRequestParams,com.xiyuan.project.model.User.class);
        String result = client.getUserNameByPost(paramUser);
        return ResultUtils.success(result);
    }
}
