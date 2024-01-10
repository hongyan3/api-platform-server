package com.xiyuan.project.model.dto.intrefaceinfo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiyuan.project.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口调用请求
 *
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {
    /**
     * 接口ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 请求参数
     */
    private String requestParams;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}