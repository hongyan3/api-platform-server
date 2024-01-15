package com.xiyuan.apicore.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiyuan.apicore.common.ErrorCode;
import com.xiyuan.apicore.constant.CommonConstant;
import com.xiyuan.apicore.exception.BusinessException;
import com.xiyuan.apicore.mapper.InterfaceInfoMapper;
import com.xiyuan.apicore.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import com.xiyuan.apicore.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.xiyuan.apicore.model.vo.InterfaceInfoVO;
import com.xiyuan.apicore.service.InterfaceInfoService;
import com.xiyuan.apicore.utils.SqlUtils;
import com.xiyuan.apiclientsdk.utils.SignatureUtils;
import com.xiyuan.apicommon.model.entity.InterfaceInfo;
import com.xiyuan.apicommon.model.entity.User;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        queryWrapper.eq("is_delete",1);
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

    @Override
    public String invoke(InterfaceInfo interfaceInfo, InterfaceInfoInvokeRequest invokeRequest,User user) {
        // 1. 构造请求
        String url = interfaceInfo.getUrl();
        String method = interfaceInfo.getMethod();
        String params = invokeRequest.getRequestParams();
        HttpRequest request = HttpUtil.createRequest(Method.valueOf(method), url);
        // 2. 参数校验
        if (StringUtils.isAnyBlank(url,method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口必要参数为空");
        }
        if (params == null) {
            params = "";
        }
        JSONObject entries = JSONUtil.parseObj(params);
        params = JSONUtil.toJsonStr(entries);
        // 3. 构造请求
        HttpResponse response = request.body(params)
                .addHeaders(getHeaderMap(params, user.getAccessKey(), user.getSecretKey()))
                .execute();
        String result = response.body();
        if (response.getStatus() != 200) {
            result = "服务器错误 StatusCode: "+response.getStatus();
        }
        // 2. 返回请求结果
        return result;
    }

    private Map<String,String> getHeaderMap(String body,String ACCESS_KEY,String SECRET_KEY) {
        Map<String,String> headers = new HashMap<>();
        headers.put("access_key",ACCESS_KEY);
        headers.put("nonce", RandomUtil.randomNumbers(6));
        headers.put("body",body);
        headers.put("timestamp",String.valueOf(System.currentTimeMillis()/1000));
        headers.put("sign", SignatureUtils.generateSignature(headers.get("body"),SECRET_KEY));
        return headers;
    }
}




