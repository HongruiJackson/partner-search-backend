package com.jackson.partnersearchbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jackson.partnersearchbackend.model.domain.User;

import java.util.List;

/**
* @author 10240
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2024-04-01 10:08:57
* @Entity com.jackson.partnersearchbackend.model.domain.User
*/
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据标签查询用户
     * 存在任一标签即满足条件
     * @param tags 前端传入的标签
     * @return 搜到的用户信息
     */
    List<User> selectAllByAnyTag(List<String> tags);

    /**
     * 根据标签查询用户
     * 满足所有的条件才满足条件
     * @param tags 前端传入的标签
     * @return 搜到的用户信息
     */
    List<User> selectAllByAllTags(String tags);


}




