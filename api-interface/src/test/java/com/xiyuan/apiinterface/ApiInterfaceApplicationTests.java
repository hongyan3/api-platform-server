package com.xiyuan.apiinterface;

import com.xiyuan.apiclientsdk.client.ApiClient;
import com.xiyuan.apiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ApiInterfaceApplicationTests {
    @Resource
    private ApiClient apiClient;

    @Test
    void test() {
        User user = new User();
        user.setUserName("xiyuan");
        String result = apiClient.getUserNameByPost(user);
        System.out.println(result);
    }
}
