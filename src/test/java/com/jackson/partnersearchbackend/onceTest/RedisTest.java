package com.jackson.partnersearchbackend.onceTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.gson.Gson;
import com.jackson.partnersearchbackend.manager.CompletedCatalogManager;
import com.jackson.partnersearchbackend.mapper.UserMapper;
import com.jackson.partnersearchbackend.model.vo.CompletedCatalogVo;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

//@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RedisTest {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Resource
    private CompletedCatalogManager completedCatalogManager;

    private static final ObjectMapper mapper = new ObjectMapper();

    private static List<CompletedCatalogVo> list = new ArrayList<>();


    @Test
    @Order(0)
    public void initial() {
        // 初始化数据
        list = completedCatalogManager.getCompletedCatalogList();
        redisTemplate.opsForValue().get("no_this_key");
        stringRedisTemplate.opsForValue().get("no_this_key");
        StopWatch stopWatch = new StopWatch();
        System.out.println(list);
    }

    @Test
    @Order(2)
    public void setRedisTemplate() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        redisTemplate.opsForValue().set("partner_search:tag:list1", list);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());

    }

    @Test
    @Order(1)
    public void setStringRedisTemplate() throws JsonProcessingException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String s = mapper.writeValueAsString(list);
        stringRedisTemplate.opsForValue().set("partner_search:tag:list",s);
        System.out.println(stopWatch.getTotalTimeMillis());
        stopWatch.stop();

    }

    @Test
    @Order(4)
    public void getRedisTemplate()  {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<CompletedCatalogVo> o =(List<CompletedCatalogVo> ) redisTemplate.opsForValue().get("partner_search:tag:list1");
        System.out.println(o);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
    @Test
    @Order(3)
    public void getStringRedisTemplate() throws JsonProcessingException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, CompletedCatalogVo.class);
        String s1 = stringRedisTemplate.opsForValue().get("partner_search:tag:list");
        List<CompletedCatalogVo> o1 = mapper.readValue(s1, listType);
        System.out.println(o1);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    @Order(5)
    public void getMysql() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        completedCatalogManager.getCompletedCatalogList();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
