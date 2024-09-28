package com.jackson.partnersearchbackend.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 创建队伍请求体
 */
@Data
public class TeamUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -2296064442443120751L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;


    /**
     * 队伍状态：0公开；1私密；2加密，默认0
     */
    private Integer teamStatus;

    /**
     * 队伍密码
     */
    private String teamPassword;
}
