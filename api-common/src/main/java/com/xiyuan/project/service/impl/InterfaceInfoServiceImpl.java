package com.xiyuan.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiyuan.project.common.ErrorCode;
import com.xiyuan.project.constant.CommonConstant;
import com.xiyuan.project.exception.BusinessException;
import com.xiyuan.project.mapper.InterfaceInfoMapper;
import com.xiyuan.project.model.dto.intrefaceinfo.InterfaceInfoQueryRequest;
import com.xiyuan.project.model.entity.InterfaceInfo;
import com.xiyuan.project.model.vo.InterfaceInfoVO;
import com.xiyuan.project.service.InterfaceInfoService;
import com.xiyuan.project.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiyuan
 * @description 针对表【interface_info(接口信息表)】的数据库操作Service实现
 * @createDate 2023-12-23 02:19:01
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo,boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String url = interfaceInfo.getUrl();
        String method = interfaceInfo.getMethod();
        if (add) {
            if (StringUtils.isAnyBlank(name,url,method)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (method != null) {
            if (!Arrays.asList("GET","POST","DELETE","PUT","OPTIONS","HEAD","TRACE","CONNECT").contains(method)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求方法类型错误");
            }
        }
    }

    @Override
    public QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (interfaceInfoQueryRequest == null) {
            return queryWrapper;
        }
        Long id = interfaceInfoQueryRequest.getId();
        String name = interfaceInfoQueryRequest.getName();
        String description = interfaceInfoQueryRequest.getDescription();
        String url = interfaceInfoQueryRequest.getUrl();
        Integer status = interfaceInfoQueryRequest.getStatus();
        String method = interfaceInfoQueryRequest.getMethod();
        Long userId = interfaceInfoQueryRequest.getUserId();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        // 拼接查询条件
        if (StringUtils.isAnyBlank(name,description,url)) {
            queryWrapper.like(ObjectUtils.isNotEmpty(name),"name",name)
                    .like(ObjectUtils.isNotEmpty(description),"description",description)
                    .like(ObjectUtils.isNotEmpty(url),"url",url);
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id),"id",id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status),"status",status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(method),"method",method);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId),"user_id",userId);
        queryWrapper.eq("is_delete",false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),sortField);
        return queryWrapper;
    }

    @Override
    public Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage) {
        Page<InterfaceInfoVO> interfaceInfoVOPage = new Page<>();
        if (CollectionUtils.isEmpty(interfaceInfoPage.getRecords())) {
            return interfaceInfoVOPage;
        }
        List<InterfaceInfoVO> list = interfaceInfoPage.getRecords().stream().map(this::getInterfaceInfoVO).collect(Collectors.toList());
        interfaceInfoVOPage.setRecords(list);
        interfaceInfoVOPage.setTotal(interfaceInfoPage.getTotal());
        return interfaceInfoVOPage;
    }

    @Override
    public InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceInfo) {
        InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
        BeanUtils.copyProperties(interfaceInfo,interfaceInfoVO);
        return interfaceInfoVO;
    }
}




