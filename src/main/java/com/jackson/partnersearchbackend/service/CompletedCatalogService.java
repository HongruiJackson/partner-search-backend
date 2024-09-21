package com.jackson.partnersearchbackend.service;

import com.jackson.partnersearchbackend.model.vo.CompletedCatalogVo;

import java.util.List;

public interface CompletedCatalogService {

    /**
     * 获取完整的标签列表，配合vant组件的格式
     * @return 有有效值的时候返回非空列表
     */
    List<CompletedCatalogVo> getCompletedCatalogList();
}
