package com.jackson.partnersearchbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jackson.partnersearchbackend.common.BaseResponse;
import com.jackson.partnersearchbackend.enums.ErrorCode;
import com.jackson.partnersearchbackend.enums.SuccessCode;
import com.jackson.partnersearchbackend.exception.BusinessException;
import com.jackson.partnersearchbackend.model.domain.User;
import com.jackson.partnersearchbackend.model.request.UserLoginRequest;
import com.jackson.partnersearchbackend.model.request.UserRegisterRequest;
import com.jackson.partnersearchbackend.service.UserService;
import com.jackson.partnersearchbackend.utils.ResultUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.jackson.partnersearchbackend.constant.UserConstant.ADMIN_ROLE;
import static com.jackson.partnersearchbackend.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/api/user")
//@CrossOrigin(origins = {"http://localhost:5173"})
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册功能
     * @param userRegisterRequest 用户注册请求体信息
     * @return 注册情况
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId = userService.userRegister(userAccount, userPassword, checkPassword);
        if (userId > 0) return ResultUtils.success(userId, SuccessCode.COMMON_SUCCESS);
        else throw new BusinessException(ErrorCode.SYSTEM_ERROR);
    }

    /**
     * 用户登录
     * @param userLoginRequest 用户登录请求体
     * @param httpServletRequest http请求，set Session的状态
     * @return 用户脱敏信息
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest) {
        if (userLoginRequest == null) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, httpServletRequest);
        return ResultUtils.success(user, SuccessCode.COMMON_SUCCESS);

    }

    /**
     * 用户注销
     * 移除session中的登录态
     * @param httpServletRequest http请求 remove
     * @return 无效值
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogOut(HttpServletRequest httpServletRequest) {
        if (httpServletRequest == null) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Integer i = userService.userLogout(httpServletRequest);

        if (i == 1) return ResultUtils.success(null, SuccessCode.COMMON_SUCCESS);
        else throw new BusinessException(ErrorCode.SYSTEM_ERROR);
    }

    /**
     * 获取用户登录态
     * @param httpServletRequest http请求
     * @return 用户信息
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest httpServletRequest) {
        Object userObj = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUserInSession = (User) userObj;
        if (currentUserInSession == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"当前用户session未知");
        }
        long userId = currentUserInSession.getId();
        User userInDB = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(userInDB);
        return ResultUtils.success(safetyUser, SuccessCode.COMMON_SUCCESS);
    }


    /**
     * 查询用户
     * 允许管理员能够模糊查询用户
     * @param userAccount 要查询的用户
     * @param httpServletRequest http请求
     * @return 脱敏用户信息列表
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String userAccount, HttpServletRequest httpServletRequest) {
        //仅管理员可查
        User  user = (User) httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        if ( user == null || user.getUserRole() != ADMIN_ROLE)
            throw new BusinessException(ErrorCode.NO_AUTH);

        List<User> userList = userService.searchUsers(userAccount);
        return ResultUtils.success(userList, SuccessCode.COMMON_SUCCESS);
    }

    /**
     * 删除用户
     * 管理员能够根据id删除用户
     * @param id 用户id
     * @param httpServletRequest http请求体
     * @return 删除是否成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest httpServletRequest) {
        //仅管理员可删除
        User  user = (User) httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        if ( user == null || user.getUserStatus() != ADMIN_ROLE)
            throw new BusinessException(ErrorCode.NO_AUTH);
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.deleteUserById(id);

        return ResultUtils.success(result, SuccessCode.COMMON_SUCCESS);
    }

    /**
     * 根据标签查找用户
     * 查找到的用户需要具备选择的所有的标签
     * @param tags 用户选中的tags
     * @return 查询到的用户
     */
    @PostMapping("/search/tags/all")
    public BaseResponse<List<User>> searchUserByTags(@RequestBody(required = false) List<String> tags) {
        List<User> userList = userService.searchUserByTags(tags);
        return ResultUtils.success(userList, SuccessCode.COMMON_SUCCESS);

    }

    /**
     * 更新用户信息
     * @param userWithNewInfo 新的用户信息
     * @param httpServletRequest 请求体，找cookie
     * @return 修改情况1代表修改成功，0代表修改无效
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User userWithNewInfo, HttpServletRequest httpServletRequest) {
        if (userWithNewInfo == null || userWithNewInfo.getUserAccount() != null) { //不允许修改用户账号
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        int result = userService.updateUser(userWithNewInfo, loginUser);
        return ResultUtils.success(result,SuccessCode.COMMON_SUCCESS);

    }

    /**
     * 主页推荐用户信息
     * @param page 页码
     * @param size 单页大小
     * @return 一页的信息
     */
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUserList(long page, long size) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        //MP的page代表的是页号，不是下标
        Page<User> userPage = userService.page(new Page<>(page, size), userQueryWrapper);
        List<User> userList = userPage.getRecords();
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).toList();
        userPage.setRecords(list);
        return ResultUtils.success(userPage, SuccessCode.COMMON_SUCCESS);

    }



}
