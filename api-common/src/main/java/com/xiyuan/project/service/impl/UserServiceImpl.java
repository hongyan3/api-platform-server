package com.xiyuan.project.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiyuan.project.common.ErrorCode;
import com.xiyuan.project.constant.CommonConstant;
import com.xiyuan.project.constant.UserConstant;
import com.xiyuan.project.exception.BusinessException;
import com.xiyuan.project.mapper.UserMapper;
import com.xiyuan.project.model.dto.user.UserQueryRequest;
import com.xiyuan.project.model.entity.User;
import com.xiyuan.project.model.enums.UserRoleEnum;
import com.xiyuan.project.model.enums.UserStatusEnum;
import com.xiyuan.project.model.vo.UserCredentialsVO;
import com.xiyuan.project.model.vo.UserVO;
import com.xiyuan.project.service.UserService;
import com.xiyuan.project.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author xiyuan
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-12-21 22:24:11
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "xiyuan";
    @Override
    public long userRegister(String userAccount, String userName,String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userName, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_account", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该账户已存在");
            }
            // 2. 加密
            String encryptPassword = EncryptPassword(userPassword);
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setPassword(encryptPassword);
            user.setUserName(userName);
            UserCredentialsVO credentialsVO = refreshCredentials(user);
            user.setAccessKey(credentialsVO.getAccessKey());
            user.setSecretKey(credentialsVO.getSecretKey());
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = EncryptPassword(userPassword);
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("password", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        if (user.getStatus() == UserStatusEnum.DISABLE.getValue()) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "该账户已禁用");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return this.getUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public String EncryptPassword(String password) {
        return DigestUtil.sha256Hex((SALT + password).getBytes());
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue()==user.getRole();
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        Integer userRole = userQueryRequest.getRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(userRole != null,"role", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userName), "user_name", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public User getUserByAccount(String account) {
        return query().eq("user_account", account).one();
    }

    @Override
    public UserCredentialsVO refreshCredentials(User user) {
        if (user == null || StringUtils.isBlank(user.getUserAccount())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        UserCredentialsVO credentials = GenerateCredentials(user.getUserAccount());
        if (credentials == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        User newUser = new User();
        BeanUtils.copyProperties(credentials,newUser);
        newUser.setId(user.getId());
        this.updateById(newUser);
        return credentials;
    }

    @Override
    public UserCredentialsVO GenerateCredentials(String userAccount) {
        String accessKey = DigestUtil.sha256Hex(UserConstant.CREDENTIALS_SALT + userAccount + RandomUtil.randomNumbers(5));
        String secretKey = DigestUtil.sha256Hex(UserConstant.CREDENTIALS_SALT + userAccount + RandomUtil.randomNumbers(8));
        UserCredentialsVO credentials = new UserCredentialsVO();
        credentials.setAccessKey(accessKey);
        credentials.setSecretKey(secretKey);
        return credentials;
    }
}

