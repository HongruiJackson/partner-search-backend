package com.jackson.partnersearchbackend.model.query;

import com.jackson.partnersearchbackend.model.request.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 查询封装类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends BasePageRequest {
    /**
     * 主键
     */
    private Long id;
    /**
     * id 列表
     */
    private List<Long> idList;
    /**
     * 搜索关键词（同时对队伍名称和描述搜索）
     */
    private String searchText;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 创建人id
     */
    private Long userId;

    /**
     * 队伍状态：0公开；1私密；2加密，默认0
     */
    private Integer teamStatus;
}
