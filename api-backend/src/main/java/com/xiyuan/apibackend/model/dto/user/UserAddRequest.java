package com.xiyuan.apibackend.model.dto.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;


@Data
public class UserAddRequest implements Serializable {
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 角色 1-普通用户 2-管理员
     */
    private Integer role;

    /**
     * 逻辑删除
     */
    @TableLogic
    private int isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
