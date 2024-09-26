package com.jackson.partnersearchbackend.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.GsonTypeHandler;
import lombok.Data;

import java.util.List;

@Data
public class UserVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 用户具有的标签
     */
    @TableField(typeHandler = GsonTypeHandler.class)
    private List<String> tags;

    /**
     * 用户简介
     */
    private String profile;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别，0 表示男性；1 表示女性
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;
}
