package com.xiyuan.apicore.model.vo;

import lombok.Data;

/**
 * @author xiyuan
 * @description ACCESS_KEY和SECRET_KEY包装类
 * @date 2024/1/9 15:57
 */

@Data
public class UserCredentialsVO {
    /**
     * access_key
     */
    private String accessKey;

    /**
     * secret_key
     */
    private String secretKey;
}
