package com.jackson.partnersearchbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jackson.partnersearchbackend.model.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author JacksonZHR
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-04-01 10:08:57
*/
public interface UserService extends IService<User> {


    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 用户二次确认的校验密码
     * @return 用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);


    /**
     * 用户登录
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request http请求对象
     * @return 脱敏的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 根据用户账号进行查询用户
     * @param userAccount 用户账号
     * @return 用户list
     */
    List<User> searchUsers(String userAccount);

    /**
     * 根据用户id删除用户
     * @param id 用户id
     * @return 删除情况
     */
    boolean deleteUserById(long id);

    /**
     * 用户信息脱敏
     * @param user 数据库用户信息
     * @return 脱敏后的用户信息
     */
    User getSafetyUser(User user);

    /**
     * 用户注销
     * @param httpServletRequest getSession
     */
    Integer userLogout(HttpServletRequest httpServletRequest);

    /**
     * 根据标签搜索用户
     * @param tags 用户搜索对象具有的标签
     * @return 搜索到的用户信息
     */
    List<User> searchUserByTags(List<String> tags);


    /**
     * 当用户登录后获取信息
     * @param request 请求体
     * @return 存储在session中的信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 更新用户
     * @param user 新的用户，即修改值
     * @param loginUser 在登录当中的用户
     * @return 数据库结果，当且仅当为1的时候修改成功
     */
    int updateUser(User user, User loginUser);
}
