package com.jackson.partnersearchbackend.service;

import com.jackson.partnersearchbackend.model.domain.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 10240
* @description 针对表【tag(标签表)】的数据库操作Service
* @createDate 2024-09-11 17:37:38
*/
public interface TagService extends IService<Tag> {

    /**
     * 通过所属目录id查找所有的Tag
     * @param catalogItemId 所属目录Id
     * @return 列表，若没有相关值返回空列表
     */
    List<Tag> getTagList(int catalogItemId);
}
