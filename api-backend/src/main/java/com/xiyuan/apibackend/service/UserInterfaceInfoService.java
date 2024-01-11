package com.xiyuan.apibackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiyuan.apibackend.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.xiyuan.apibackend.model.entity.UserInterfaceInfo;
import com.xiyuan.apibackend.model.vo.UserInterfaceInfoVO;

/**
* @author xiyuan
* @description 针对表【user_interface_info(用户接口调用关系表)】的数据库操作Service
* @createDate 2024-01-11 10:19:22
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    /**
     * 校验
     *
     * @param userInterfaceInfo
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 获取查询条件
     *
     * @param userInterfaceInfoQueryRequest
     * @return
     */
    QueryWrapper<UserInterfaceInfo> getQueryWrapper(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest);


    /**
     * 分页获取接口封装
     *
     * @param userInterfaceInfoPage
     * @return
     */
    Page<UserInterfaceInfoVO> getUserInterfaceInfoVOPage(Page<UserInterfaceInfo> userInterfaceInfoPage);
    /**
     * 获取接口封装
     *
     * @param userInterfaceInfo
     * @return
     */
    UserInterfaceInfoVO getUserInterfaceInfoVO(UserInterfaceInfo userInterfaceInfo);

    boolean invokeCount(Long interfaceInfoId,Long userId);
}
