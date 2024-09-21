package com.jackson.partnersearchbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.partnersearchbackend.model.domain.Catalog;
import com.jackson.partnersearchbackend.service.CatalogService;
import com.jackson.partnersearchbackend.mapper.CatalogMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author 10240
* @description 针对表【catalog(类别表)】的数据库操作Service实现
* @createDate 2024-09-11 17:36:58
*/
@Service
public class CatalogServiceImpl extends ServiceImpl<CatalogMapper, Catalog>
    implements CatalogService{

    @Resource
    private CatalogMapper catalogMapper;

    @Override
    public Catalog getCatalogItem(int id) {
        return catalogMapper.selectById(id);
    }
}




