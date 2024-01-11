package com.xiyuan.apiclientsdk;

import com.xiyuan.apiclientsdk.client.ApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiyuan
 * @className ApiClientConfig
 * @description apiClient自动装配配置类
 * @date 2024/1/8 14:29
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "api.client")
@ComponentScan
public class ApiClientConfig {

    private String ACCESS_KEY;
    private String SECRET_KEY;

    @Bean
    public ApiClient apiClient() {
        return new ApiClient(ACCESS_KEY,SECRET_KEY);
    }
}
