package com.xiyuan.apibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiyuan.apibackend.common.ErrorCode;
import com.xiyuan.apibackend.constant.CommonConstant;
import com.xiyuan.apibackend.exception.BusinessException;
import com.xiyuan.apibackend.mapper.UserInterfaceInfoMapper;
import com.xiyuan.apibackend.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.xiyuan.apibackend.model.entity.UserInterfaceInfo;
import com.xiyuan.apibackend.model.vo.UserInterfaceInfoVO;
import com.xiyuan.apibackend.service.UserInterfaceInfoService;
import com.xiyuan.apibackend.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author xiyuan
* @description 针对表【user_interface_info(用户接口调用关系表)】的数据库操作Service实现
* @createDate 2024-01-11 10:19:22
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfo.getUserId() <= 0 || userInterfaceInfo.getInterfaceInfoId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口或用户不存在");
            }
        }
        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"剩余次数不能小于0");
        }
    }

    @Override
    public QueryWrapper<UserInterfaceInfo> getQueryWrapper(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (userInterfaceInfoQueryRequest == null) {
            return queryWrapper;
        }
        Long id = userInterfaceInfoQueryRequest.getId();
        Long userId = userInterfaceInfoQueryRequest.getUserId();
        Long interfaceInfoId = userInterfaceInfoQueryRequest.getInterfaceInfoId();
        Integer total = userInterfaceInfoQueryRequest.getTotal();
        Integer leftNum = userInterfaceInfoQueryRequest.getLeftNum();
        Integer status = userInterfaceInfoQueryRequest.getStatus();
        String sortField = userInterfaceInfoQueryRequest.getSortField();
        String sortOrder = userInterfaceInfoQueryRequest.getSortOrder();
        
        queryWrapper.eq(ObjectUtils.isNotEmpty(id),"id",id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId),"user_id",userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(interfaceInfoId),"interface_info_id",interfaceInfoId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(total),"total",total);
        queryWrapper.eq(ObjectUtils.isNotEmpty(leftNum),"left_num",leftNum);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status),"status",status);
        queryWrapper.eq("is_delete",1);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),sortField);
        return queryWrapper;
    }

    @Override
    public Page<UserInterfaceInfoVO> getUserInterfaceInfoVOPage(Page<UserInterfaceInfo> userInterfaceInfoPage) {
        Page<UserInterfaceInfoVO> userInterfaceInfoVOPage = new Page<>();
        if (CollectionUtils.isEmpty(userInterfaceInfoPage.getRecords())) {
            return userInterfaceInfoVOPage;
        }
        List<UserInterfaceInfoVO> list = userInterfaceInfoPage.getRecords().stream().map(this::getUserInterfaceInfoVO).collect(Collectors.toList());
        userInterfaceInfoVOPage.setRecords(list);
        userInterfaceInfoVOPage.setTotal(userInterfaceInfoPage.getTotal());
        return userInterfaceInfoVOPage;
    }

    @Override
    public UserInterfaceInfoVO getUserInterfaceInfoVO(UserInterfaceInfo userInterfaceInfo) {
        UserInterfaceInfoVO userInterfaceInfoVO = new UserInterfaceInfoVO();
        BeanUtils.copyProperties(userInterfaceInfo,userInterfaceInfoVO);
        return userInterfaceInfoVO;
    }

    @Override
    public boolean invokeCount(Long interfaceInfoId, Long userId) {
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interface_info_id",interfaceInfoId);
        updateWrapper.eq("user_id",userId);
        updateWrapper.gt("left_num",0);
        updateWrapper.setSql("left_num = left_num - 1,total = total + 1");
        return this.update(updateWrapper);
    }
}




