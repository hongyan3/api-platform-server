package com.xiyuan.apicore.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @author xiyuan
 * @description 接口信息枚举
 * @date 2024/1/9 09:02
 */
@Getter
public enum InterfaceInfoStatusEnum {
    ONLINE("上线",1),
    OFFLINE("下线",0);
    private final String text;
    private final int value;
    InterfaceInfoStatusEnum(String text,Integer value) {
        this.text = text;
        this.value = value;
    }
    /**
     *根据value获取枚举
     */
    public static InterfaceInfoStatusEnum getEnumByStatusCode(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (InterfaceInfoStatusEnum statusEnum : InterfaceInfoStatusEnum.values()) {
            if (statusEnum.value == value) {
                return statusEnum;
            }
        }
        return null;
    }
}
