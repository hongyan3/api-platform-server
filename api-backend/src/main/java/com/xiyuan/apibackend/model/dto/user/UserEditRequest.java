package com.xiyuan.apibackend.model.dto.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class UserEditRequest {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 性别 1-男 2-女 3-保密
     */
    private Integer gender;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
