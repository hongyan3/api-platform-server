package com.xiyuan.apiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.xiyuan.apiclientsdk.model.User;
import com.xiyuan.apiclientsdk.utils.SignatureUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 调用第三方接口的客户端
 *
 * @author xiyaun
 */
public class ApiClient {
    private final String ACCESS_KEY;
    private final String SECRET_KEY;
    public ApiClient(String accessKey, String secretKey) {
        this.ACCESS_KEY = accessKey;
        this.SECRET_KEY = secretKey;
    }
    private Map<String,String> getHeaderMap(String body) {
        Map<String,String> headers = new HashMap<>();
        headers.put("access_key",ACCESS_KEY);
        headers.put("nonce", RandomUtil.randomNumbers(6));
        headers.put("body",body);
        headers.put("timestamp",String.valueOf(System.currentTimeMillis()/1000));
        headers.put("sign", SignatureUtils.generateSignature(headers.get("body"),SECRET_KEY));
        return headers;
    }

    public String getUserNameByPost(User user) {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post("http://127.0.0.1:8082/api/name")
                .body(json)
                .addHeaders(getHeaderMap(json))
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        return result;
    }
}
