package com.spzx.common.redis.cache;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 16/7/2024 16:55 周二
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Slf4j
@Aspect
@Component
public class RedisCacheAspect {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 环绕通知：对所有业务模块任意方法使用自定义注解缓存方法进行增强，增强逻辑：
     * 1.优先从缓存redis中获取业务数据
     * 2.未命中缓存，获取分布式锁
     * 3.执行目标方法（查询数据库方法）
     * 4.将锁释放
     *
     * @param pjp 可以操作增强的方法，比如执行业务方法，比如获取业务方法参数等等
     * @param
     * @return
     * @throws Throwable
     */
    @SneakyThrows  // 不需要在lambda表达式中显式声明注解
    @Around("@annotation(redisCache)")
    public Object redisCacheAdvice(ProceedingJoinPoint pjp,RedisCache redisCache){
        //1 根据注解前缀 + 业务方法参数值构建数据在redis的key名字
        // 获取注解前缀
        String prefix = redisCache.prefix();
        // 获取业务方法里面参数列表
        Object[] args = pjp.getArgs();
        String paramVal = "data";
        // 把业务方法里面多个参数值获取到 之间  ： 隔开
        if(args!=null&&args.length>0){
            List<Object> list = Arrays.asList(args);
            paramVal = list.stream()
                    .map(arg -> arg.toString())
                    .collect(Collectors.joining(":"));
        }

        //构建key
        String dataKey = prefix + paramVal;
        //2 根据第一步构建key的名字查询redis
        Object resultObject = redisTemplate.opsForValue().get(dataKey);
        //3 如果redis查询到数据，直接返回
        if(resultObject!=null) return resultObject;
        //4 如果redis查询不到数据，查询mysql
        //4.1 添加分布式锁
        String lockKey = dataKey + ":lock";
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean flag = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if(flag){
                resultObject  = pjp.proceed();

                redisTemplate.opsForValue().set(dataKey,resultObject,10,TimeUnit.MINUTES);

                return  resultObject;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        return null;

    }

}
