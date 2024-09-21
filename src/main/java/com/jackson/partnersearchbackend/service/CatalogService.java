package com.jackson.partnersearchbackend.service;

import com.jackson.partnersearchbackend.model.domain.Catalog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 10240
* @description 针对表【catalog(类别表)】的数据库操作Service
* @createDate 2024-09-11 17:36:58
*/
public interface CatalogService extends IService<Catalog> {

    /**
     * 根据id查询的类别项
     * @param id 类别的id
     * @return
     */
    Catalog getCatalogItem(int id);
}
