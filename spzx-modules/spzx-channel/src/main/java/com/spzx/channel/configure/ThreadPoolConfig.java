package com.spzx.channel.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 17/7/2024 11:32 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        // 当前系统可用处理器数量
        int processorsCount = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                processorsCount * 2,  //线程池的核心线程数量
                processorsCount * 2,             //线程池的最大线程数
                0,                               //当线程数大于核心线程数时，多余的空闲线程存活的最长时间
                TimeUnit.SECONDS,                //时间单位
                new ArrayBlockingQueue<>(200),  //任务队列，用来储存等待执行任务的队列
                Executors.defaultThreadFactory(),      //默认的线程工厂，用来创建线程.
                // 自定义拒绝策略
                (runnable, executor) -> {           //拒绝策略，当提交的任务过多而不能及时处理时，我们可以定制策略来处理任务
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    executor.submit(runnable);
                }
        );
        // 线程池创建,核心线程同时创建
        threadPoolExecutor.prestartAllCoreThreads();
        return threadPoolExecutor;
    }
}
