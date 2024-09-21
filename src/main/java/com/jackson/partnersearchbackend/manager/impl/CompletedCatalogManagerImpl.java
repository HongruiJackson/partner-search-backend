package com.jackson.partnersearchbackend.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jackson.partnersearchbackend.manager.CompletedCatalogManager;
import com.jackson.partnersearchbackend.mapper.CatalogMapper;
import com.jackson.partnersearchbackend.mapper.TagMapper;
import com.jackson.partnersearchbackend.model.domain.Catalog;
import com.jackson.partnersearchbackend.model.domain.Tag;
import com.jackson.partnersearchbackend.model.vo.CompletedCatalogVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class CompletedCatalogManagerImpl implements CompletedCatalogManager {

    @Resource
    private CatalogMapper catalogMapper;

    @Resource
    private TagMapper tagMapper;

    @Override
    public List<CompletedCatalogVo> getCompletedCatalogList() {
        LambdaQueryWrapper<Catalog> catalogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<Catalog> catalogList = catalogMapper.selectList(catalogLambdaQueryWrapper);
        if (catalogList == null) return new ArrayList<>(); //父为空那就没必要查子了

        ArrayList<CompletedCatalogVo> completedCatalogVoList = new ArrayList<>();
        catalogList.forEach(catalog -> {
            LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Tag::getCatalogItemId,catalog.getId());
            List<Tag> tagList = tagMapper.selectList(queryWrapper);
            if (tagList!=null) completedCatalogVoList.add(new CompletedCatalogVo(catalog,tagList));
        });
        return completedCatalogVoList;

    }
}
