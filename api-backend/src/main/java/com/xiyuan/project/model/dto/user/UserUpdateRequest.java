package com.xiyuan.project.model.dto.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

@Data
public class UserUpdateRequest {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 性别 1-男 2-女 3-保密
     */
    private Integer gender;

    /**
     * 角色 1-普通用户 2-管理员
     */
    private Integer role;

    /**
     * 用户状态 1-正常 2-禁用
     */
    private Integer status;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private int isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
