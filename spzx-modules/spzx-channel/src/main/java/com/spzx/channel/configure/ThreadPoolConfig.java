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
                processorsCount*2,
                processorsCount*2,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(200),
                Executors.defaultThreadFactory(),
                // 自定义拒绝策略
                ((runnable, executor) -> {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                    }
                    //再次将拒绝任务提交给线程池执行
                    executor.submit(runnable);
                })
        );
        // 线程池创建,核心线程同时创建
        threadPoolExecutor.prestartAllCoreThreads();
        return threadPoolExecutor;
    }
}
