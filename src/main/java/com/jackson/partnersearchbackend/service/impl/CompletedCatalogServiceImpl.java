package com.jackson.partnersearchbackend.service.impl;

import com.jackson.partnersearchbackend.manager.CompletedCatalogManager;
import com.jackson.partnersearchbackend.model.vo.CompletedCatalogVo;
import com.jackson.partnersearchbackend.service.CompletedCatalogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CompletedCatalogServiceImpl implements CompletedCatalogService {

    @Resource
    CompletedCatalogManager completedCatalogManager;


    @Override
    public List<CompletedCatalogVo> getCompletedCatalogList() {
        return completedCatalogManager.getCompletedCatalogList();
    }
}
