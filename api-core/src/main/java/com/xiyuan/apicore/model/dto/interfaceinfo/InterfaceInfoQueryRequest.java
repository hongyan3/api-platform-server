package com.xiyuan.apicore.model.dto.interfaceinfo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiyuan.apicore.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口信息表
 * @TableName interface_info
 */
@TableName(value ="interface_info")
@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {
    /**
     * 接口ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 接口url
     */
    private String url;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 状态 0-关闭 1-开启
     */
    private Integer status;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 用户ID
     */
    private Long userId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}