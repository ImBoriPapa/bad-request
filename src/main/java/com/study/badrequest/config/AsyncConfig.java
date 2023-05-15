package com.study.badrequest.config;

import com.study.badrequest.handler.CustomAsyncExceptionHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    public static final String ACTIVITY_ASYNC_EXECUTOR = "activityAsyncExecutor";
    public static final String QUESTION_IMAGE_ASYNC_EXECUTOR = "questionImageAsyncExecutor";
    private int CORE_POOL_SIZE = 3;
    private int MAX_POOL_SIZE = 10;
    private int QUEUE_CAPACITY = 100_1000;

    @Bean(name = ACTIVITY_ASYNC_EXECUTOR)
    public Executor threadPoolTaskExecutor1() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix("act-exec-" + executor.getActiveCount());
        return executor;
    }

    @Bean(name = QUESTION_IMAGE_ASYNC_EXECUTOR)
    public Executor threadPoolTaskExecutor2() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix("qi-exec-" + executor.getActiveCount());
        return executor;
    }
    
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }
}
