package com.xiyuan.apigateway.controller;

import com.xiyuan.apicommon.common.BaseResponse;
import com.xiyuan.apicommon.common.ResultUtils;
import com.xiyuan.apigateway.model.IpAddRequest;
import com.xiyuan.apigateway.service.IpBlackListService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xiyuan
 * @description TODO
 * @date 2024/3/29 14:24
 */
@RestController
@RequestMapping("/gateway")
public class ipBlackListController {
    @Resource
    IpBlackListService ipService;

    @GetMapping("/black_list")
    public BaseResponse<List<String>> getIpBlackList() {
        return ResultUtils.success(ipService.getIpBlackList());
    }
    @PostMapping("/black_list")
    public BaseResponse<Boolean> addBlackIp(@RequestBody IpAddRequest request) {
        return ResultUtils.success(ipService.addBlackIp(request.getIp()));
    }
    @DeleteMapping("/black_list/{ip}")
    public BaseResponse<Boolean> deleteBlackIp(@PathVariable String ip) {
        return ResultUtils.success(ipService.deleteBlackIp(ip));
    }
}
