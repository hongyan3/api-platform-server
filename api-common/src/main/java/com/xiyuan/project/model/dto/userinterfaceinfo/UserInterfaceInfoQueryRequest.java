package com.xiyuan.project.model.dto.userinterfaceinfo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiyuan.project.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息表
 * @TableName interface_info
 */
@TableName(value ="user_interface_info")
@Data
public class UserInterfaceInfoQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 调用用户id
     */
    private Long userId;

    /**
     * 调用接口id
     */
    private Long interfaceInfoId;

    /**
     * 调用总次数
     */
    private Integer total;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 调用状态 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}