package com.jackson.partnersearchbackend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.jackson.partnersearchbackend.constant.GlobalConstant;
import com.jackson.partnersearchbackend.manager.CompletedCatalogManager;
import com.jackson.partnersearchbackend.model.vo.CompletedCatalogVo;
import com.jackson.partnersearchbackend.service.CompletedCatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.jackson.partnersearchbackend.constant.GlobalConstant.GLOBAL_REDIS_KEY;
import static com.jackson.partnersearchbackend.constant.TagConstant.TAG_LIST_REDIS_KEY;
import static com.jackson.partnersearchbackend.constant.TagConstant.TAG_REDIS_KEY;

@Service
@Slf4j
public class CompletedCatalogServiceImpl implements CompletedCatalogService {

    @Resource
    private CompletedCatalogManager completedCatalogManager;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public List<CompletedCatalogVo> getCompletedCatalogList()  {
        String redisKey = GLOBAL_REDIS_KEY+TAG_REDIS_KEY+TAG_LIST_REDIS_KEY;
        String stringList = stringRedisTemplate.opsForValue().get(redisKey);
        if (stringList==null) {
            List<CompletedCatalogVo> completedCatalogList = completedCatalogManager.getCompletedCatalogList();
            try {
                stringRedisTemplate.opsForValue().set(redisKey,mapper.writeValueAsString(completedCatalogList));
            } catch (JsonProcessingException e) {
                log.error("缓存插入失败，可能是JSON序列化的问题");
//                throw new RuntimeException(e);
                return completedCatalogList;
            }
            return completedCatalogList;
        }
        try {
            CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, CompletedCatalogVo.class);
            return mapper.readValue(stringList,listType);
        } catch (JsonProcessingException e) {
            return completedCatalogManager.getCompletedCatalogList();
        }
    }
}
