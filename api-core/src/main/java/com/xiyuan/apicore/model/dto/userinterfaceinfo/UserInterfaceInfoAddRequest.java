package com.xiyuan.apicore.model.dto.userinterfaceinfo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口信息表
 * @TableName interface_info
 */
@TableName(value ="user_interface_info")
@Data
public class UserInterfaceInfoAddRequest implements Serializable {
    /**
     * 调用用户id
     */
    private Long userId;

    /**
     * 调用接口id
     */
    private Long interfaceInfoId;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 调用状态 0-禁用 1-启用
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}