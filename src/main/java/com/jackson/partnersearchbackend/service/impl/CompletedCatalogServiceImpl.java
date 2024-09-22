package com.jackson.partnersearchbackend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.jackson.partnersearchbackend.manager.CompletedCatalogManager;
import com.jackson.partnersearchbackend.model.vo.CompletedCatalogVo;
import com.jackson.partnersearchbackend.service.CompletedCatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
        String stringList = stringRedisTemplate.opsForValue().get("partner_search:tag:list");
        if (stringList==null) {
            List<CompletedCatalogVo> completedCatalogList = completedCatalogManager.getCompletedCatalogList();
            try {
                stringRedisTemplate.opsForValue().set("partner_search:tag:list",mapper.writeValueAsString(completedCatalogList));
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
