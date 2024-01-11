package com.xiyuan.project.controller;

import com.xiyuan.project.model.User;
import com.xiyuan.project.utils.SignatureUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 名称API
 *
 * @author xiyuan
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/")
    public String getNameByGet(String name,HttpServletRequest request) {
        String source = request.getHeader("source");
        if (source == null || source.equals("")) {
            return "请求来源不合法";
        }
        return "GET 你的名字是"+name+"请求来源："+source;
    }

    @PostMapping("/")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是"+name;
    }

    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request) {
        String accessKey = request.getHeader("access_key");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        String body = request.getHeader("body");
        if (!accessKey.equals("267cc5dd83ce69498b24b47769bea8a768e7deb0f5ac6112e9a3464d6f1a29f1")) {
            throw new RuntimeException("无权限");
        }
        if (Long.parseLong(nonce) > 1000000) {
            throw new RuntimeException("无权限");
        }
        long now = System.currentTimeMillis()/1000;
        if (now - Long.parseLong(timestamp) > 30) {
            throw new RuntimeException("无权限");
        }
        if (!SignatureUtils.generateSignature(body,"c19a55457eaf5a9f1248ff277fd884f21cd20636234392a487109ba1c3facd22").equals(sign)) {
            throw new RuntimeException("无权限");
        }
        return "POST 你的名字是"+user.getUserName();
    }
}
