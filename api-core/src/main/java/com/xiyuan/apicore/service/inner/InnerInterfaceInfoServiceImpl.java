package com.xiyuan.apicore.service.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiyuan.apicommon.model.enums.ErrorCode;
import com.xiyuan.apicommon.exception.BusinessException;
import com.xiyuan.apicore.mapper.InterfaceInfoMapper;
import com.xiyuan.apicore.mapper.UserMapper;
import com.xiyuan.apicommon.model.entity.InterfaceInfo;
import com.xiyuan.apicommon.model.entity.User;
import com.xiyuan.apicommon.service.InnerInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author xiyuan
 * @description TODO
 * @date 2024/1/11 21:03
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;
    @Override
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isBlank(accessKey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("access_key",accessKey);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url,method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url",url);
        queryWrapper.eq("method",method);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }
}
