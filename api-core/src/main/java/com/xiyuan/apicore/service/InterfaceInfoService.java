package com.xiyuan.apicore.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiyuan.apicore.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.xiyuan.apicore.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.xiyuan.apicore.model.vo.InterfaceInfoVO;
import com.xiyuan.apicommon.model.entity.InterfaceInfo;
import com.xiyuan.apicommon.model.entity.User;

/**
* @author xiyuan
* @description 针对表【interface_info(接口信息表)】的数据库操作Service
* @createDate 2023-12-23 02:19:01
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 校验
     *
     * @param interfaceInfo
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo,boolean add);

    /**
     * 获取查询条件
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest);


    /**
     * 分页获取接口封装
     *
     * @param interfaceInfoPage
     * @return
     */
    Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage);
    /**
     * 获取接口封装
     *
     * @param interfaceInfo
     * @return
     */
    InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceInfo);

    /**
     * 调用接口
     * @param interfaceInfo 接口信息
     * @param user 调用用户信息
     * @return 调用结果
     */
    String invoke(InterfaceInfo interfaceInfo, InterfaceInfoInvokeRequest invokeRequest, User user);
}
