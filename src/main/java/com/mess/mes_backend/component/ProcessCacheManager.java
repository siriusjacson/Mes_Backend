package com.mess.mes_backend.component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mess.mes_backend.entity.ProcessLinkTpl;
import com.mess.mes_backend.mapper.ProcessLinkTplMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ProcessCacheManager {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ProcessLinkTplMapper linkMapper;

    // 缓存 Key 前缀: process_dag:model_id
    private static final String KEY_PREFIX = "process_dag:";

    /**
     * 获取某型号的完整 DAG 结构 (优先查 Redis，没有则回源数据库并缓存)
     * 性能提升：10ms vs 2ms
     */
    @SuppressWarnings("unchecked")
    public List<ProcessLinkTpl> getProcessLinks(Long modelId) {
        String key = KEY_PREFIX + modelId;
        
        // 1. 查缓存
        Object cachedValue = redisTemplate.opsForValue().get(key);
        if (cachedValue != null) {
            // Redis 存的是 List，强转回来
            return (List<ProcessLinkTpl>) cachedValue;
        }

        // 2. 缓存未命中（击穿）：查数据库
        List<ProcessLinkTpl> links = linkMapper.selectList(
                new QueryWrapper<ProcessLinkTpl>().eq("model_id", modelId)
        );

        // 3. 写入缓存 (设置 24小时过期，防止死数据)
        if (!links.isEmpty()) {
            redisTemplate.opsForValue().set(key, links, 24, TimeUnit.HOURS);
        }
        
        return links;
    }

    /**
     * 当管理员修改工艺时，调用此方法清除缓存
     */
    public void evictCache(Long modelId) {
        redisTemplate.delete(KEY_PREFIX + modelId);
    }
}
