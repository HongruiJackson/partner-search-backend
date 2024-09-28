package com.jackson.partnersearchbackend.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 加入队伍请求体
 */
@Data
public class TeamJoinRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 605763474227982813L;

    /**
     * 队伍主键id
     */
    private Long id;

    /**
     * 队伍密码
     */
    private String teamPassword;
}
