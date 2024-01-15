package com.xiyuan.apicore.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @author xiyuan
 * @description TODO
 * @date 2024/1/10 21:16
 */
@Getter
public enum UserStatusEnum {
    DISABLE("禁用",0),
    ENABLE("启用",1);

    private final String text;
    private final int value;
    UserStatusEnum(String text,int value) {
        this.text = text;
        this.value = value;
    }
    /**
     *根据roleCode获取枚举
     */
    public static UserStatusEnum getEnumByStatusCode(Integer statusCode) {
        if (ObjectUtils.isEmpty(statusCode)) {
            return null;
        }
        for (UserStatusEnum status : UserStatusEnum.values()) {
            if (status.value == statusCode) {
                return status;
            }
        }
        return null;
    }
}
