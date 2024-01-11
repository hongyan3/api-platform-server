package com.xiyuan.apicommon.service;

import com.xiyuan.apicommon.model.entity.InterfaceInfo;
import com.xiyuan.apicommon.model.entity.User;

/**
 * @author xiyuan
 * @interfaceName ApiBackendService
 * @description 公共远程调用接口
 * @date 2024/1/11 21:13
 */
public interface InnerInterfaceInfoService {
    /**
     * 根据accessKey和secretKey获取接口调用用户的信息，判断其是否有权限
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);

    /**
     * 根据URL和Method获取接口信息
     * @param url
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String url,String method);
}
