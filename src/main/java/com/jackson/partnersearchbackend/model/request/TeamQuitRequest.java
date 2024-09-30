package com.jackson.partnersearchbackend.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 退出队伍请求体
 */
@Data
public class TeamQuitRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 605763474227982813L;

    /**
     * 队伍主键id
     */
    private Long id;
}
