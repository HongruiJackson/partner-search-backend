package com.jackson.partnersearchbackend.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.jackson.partnersearchbackend.manager.CompletedCatalogManager;
import com.jackson.partnersearchbackend.model.vo.CompletedCatalogVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jackson.partnersearchbackend.constant.GlobalConstant.GLOBAL_REDIS_KEY;
import static com.jackson.partnersearchbackend.constant.TagConstant.*;

/**
 * 缓存预热
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private CompletedCatalogManager completedCatalogManager;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Scheduled(cron = "0 0 0 * * ?")
    public void doCacheTagList() {
        String lockKey = GLOBAL_REDIS_KEY+TAG_REDIS_KEY+TAG_LIST_RRE_CACHE_LOCK_KEY;
        RLock redissonClientLock = redissonClient.getLock(lockKey);

        try {
            //看门狗机制
            if (redissonClientLock.tryLock(0,1,TimeUnit.MINUTES)) {
                String redisKey = GLOBAL_REDIS_KEY + TAG_REDIS_KEY + TAG_LIST_REDIS_KEY;
                List<CompletedCatalogVo> completedCatalogList = completedCatalogManager.getCompletedCatalogList();

                try {
                    stringRedisTemplate.opsForValue().set(redisKey, mapper.writeValueAsString(completedCatalogList), 24, TimeUnit.HOURS);
                    LocalDateTime date = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd 'at' HH:mm:ss");
                    String time = formatter.format(date);
                    log.info("缓存成功：{}",time);
                } catch (JsonProcessingException e) {
                    log.error("缓存失败");
                    throw new RuntimeException(e);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (redissonClientLock.isHeldByCurrentThread()){
                redissonClientLock.unlock();
            }
        }
    }
}
