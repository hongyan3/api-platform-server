package com.xiyuan.project.model.enums;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum FileUploadBusinessEnum {
    USER_AVATAR("用户头像","user_avatar");
    private final String text;
    private final String value;
    FileUploadBusinessEnum(String text,String value) {
        this.text = text;
        this.value = value;
    }
    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static FileUploadBusinessEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (FileUploadBusinessEnum anEnum : FileUploadBusinessEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
