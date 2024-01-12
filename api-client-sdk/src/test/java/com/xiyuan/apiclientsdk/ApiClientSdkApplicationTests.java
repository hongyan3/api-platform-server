package com.xiyuan.apiclientsdk;

import com.xiyuan.apiclientsdk.client.ApiClient;
import com.xiyuan.apiclientsdk.model.User;
import org.junit.jupiter.api.Test;


class ApiClientSdkApplicationTests {
    @Test
    void demo() {
        ApiClient client = new ApiClient("1a22b626187d160a650af29a166f0fa16d5f81f609bf1f63d64aa8c448ba4568", "99fa7ea288b819b05b21f8f89f0d963a2e5f5ab9baa2ba0d9a8bb6dfe12dbcbb");
        User user = new User();
        user.setUserName("zhanghongyan");
        String userNameByPost = client.getUserNameByPost(user);
        System.out.println(userNameByPost);
    }
}
