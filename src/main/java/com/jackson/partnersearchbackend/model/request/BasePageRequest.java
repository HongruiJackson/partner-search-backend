package com.jackson.partnersearchbackend.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用分页请求参数
 */
@Data
public class BasePageRequest {
    /**
     * 页面大小
     */
    protected Integer pageSize = 20;

    /**
     * 页码
     */
    protected Integer pageNum = 1;
}
