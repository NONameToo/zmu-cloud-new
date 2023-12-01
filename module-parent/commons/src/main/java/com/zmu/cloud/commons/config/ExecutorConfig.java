package com.zmu.cloud.commons.config;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.*;

/**
 * 线程池配置  定时任务，异步任务
 * @author YH
 */
@Slf4j
@EnableAsync
@EnableScheduling
@Configuration
public class ExecutorConfig implements SchedulingConfigurer, AsyncConfigurer {

    /**
     * 定时任务使用的线程池
     * @return
     */
    @Bean(destroyMethod = "shutdown", name = "taskScheduler")
    public ThreadPoolTaskScheduler taskScheduler(){
        log.info("创建定时任务调度线程池 start");
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(150);
        scheduler.setThreadNamePrefix("task-");
        scheduler.setAwaitTerminationSeconds(600);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        log.info("创建定时任务调度线程池 end");
        return scheduler;
    }

    /**
     * 异步任务执行线程池
     * @return
     */
    @Primary
    @Bean(name = "asyncExecutor")
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(9);
        executor.setQueueCapacity(2000);
        executor.setKeepAliveSeconds(60);
        executor.setMaxPoolSize(200);
        executor.setThreadNamePrefix("taskExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        /*executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            private DelayQueue<PullJobDelayTask> queue = new DelayQueue<>();
            private int i = 0;
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                if(r instanceof MyThread){
                    MyThread thread = (MyThread) r;
                    log.info("异常线程参数：{}",thread);
                }
                queue.offer(new PullJobDelayTask(20, TimeUnit.SECONDS, r));
                log.info("等待20秒...");
                if(i>0){
                    return;
                }
                CompletableFuture.runAsync(()-> {
                    log.info("新增线程池...");
                    while (true) {
                        try {
                            log.info("拉去任务...");
                            PullJobDelayTask task = queue.take();
                            executor.execute(task.getRunnable());
                        } catch (Exception e) {
                            log.error("抛出异常,{}", e);
                        }
                    }
                });
                i++;

            }
        });*/
        executor.initialize();
        return executor;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = taskScheduler();
        scheduledTaskRegistrar.setTaskScheduler(taskScheduler);
    }

    @Override
    public Executor getAsyncExecutor() {
        return asyncExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            throwable.printStackTrace();
            String msg = String.format("异步任务执行出现异常, message {}, emthod {}, params {}", throwable, method, objects);
            log.error(msg, throwable);
        };
    }
}