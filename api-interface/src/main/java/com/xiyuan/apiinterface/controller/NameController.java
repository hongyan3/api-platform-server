package com.xiyuan.apiinterface.controller;

import com.xiyuan.apiclientsdk.model.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 名称API
 *
 * @author xiyuan
 */
@RestController
@RequestMapping("/name")
public class NameController {
    @PostMapping
    public String getNameByPost(@RequestBody User user,HttpServletRequest request) {
        String source = request.getHeader("resource");
        return "GET 你的名字是: "+user.getUserName()+" 请求来源: "+source;
    }
}
