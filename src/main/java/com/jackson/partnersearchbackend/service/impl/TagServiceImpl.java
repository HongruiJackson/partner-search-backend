package com.jackson.partnersearchbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.partnersearchbackend.model.domain.Tag;
import com.jackson.partnersearchbackend.service.TagService;
import com.jackson.partnersearchbackend.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author 10240
* @description 针对表【tag(标签表)】的数据库操作Service实现
* @createDate 2024-09-11 17:37:38
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




