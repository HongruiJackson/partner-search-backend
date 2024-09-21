package com.jackson.partnersearchbackend.manager;

import com.jackson.partnersearchbackend.model.vo.CompletedCatalogVo;

import java.util.List;

public interface CompletedCatalogManager {

    /**
     * 获取完整的父子列表
     * @return 列表
     */
    List<CompletedCatalogVo> getCompletedCatalogList();

}
