package com.xiyuan.apicore.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiyuan.apicore.model.dto.user.UserQueryRequest;
import com.xiyuan.apicore.model.vo.UserCredentialsVO;
import com.xiyuan.apicore.model.vo.UserVO;
import com.xiyuan.apicommon.model.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author xiyuan
* @description 针对表【user】的数据库操作Service
* @createDate 2023-12-21 22:24:11
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount,String userName, String userPassword, String checkPassword);
    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);
    /**
     * 获取当前登录用户
     *
     * @param request
     * @return 用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param account
     * @return 用户信息
     */
    User getUserByAccount(String account);

    /**
     * 获取密码加密
     * @param password
     * @return
     */
    String EncryptPassword(String password);

    /**
     * 获取密码加密
     * @param userAccount
     * @return
     */
    UserCredentialsVO GenerateCredentials(String userAccount);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);
    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);
    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);
    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    UserVO getUserVO(User user);
    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);
    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 刷新ACCESS_KEY和SECRET_TOKEN
     *
     * @return
     */

    UserCredentialsVO refreshCredentials(User user);

}
