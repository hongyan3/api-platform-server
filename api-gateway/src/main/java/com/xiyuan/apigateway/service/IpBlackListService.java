package com.xiyuan.apigateway.service;

import com.xiyuan.apicommon.exception.BusinessException;
import com.xiyuan.apicommon.model.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author xiyuan
 * @description TODO
 * @date 2024/3/29 13:52
 */
@Service
@Slf4j
public class IpBlackListService {
    public static final String GATEWAY_IP_BLACK_LIST = "api:gateway:ip_black_list";
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public List<String> getIpBlackList() {
        Set<String> ipSet = stringRedisTemplate.opsForSet().members(GATEWAY_IP_BLACK_LIST);
        if (CollectionUtils.isEmpty(ipSet)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(ipSet);
    }
    public boolean addBlackIp(String ip) {
        Long count = stringRedisTemplate.opsForSet().add(GATEWAY_IP_BLACK_LIST, ip);
        if (count == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return count > 0;
    }
    public boolean deleteBlackIp(String ip) {
        Long count = stringRedisTemplate.opsForSet().remove(GATEWAY_IP_BLACK_LIST, ip);
        if (count == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return count > 0;
    }
}
