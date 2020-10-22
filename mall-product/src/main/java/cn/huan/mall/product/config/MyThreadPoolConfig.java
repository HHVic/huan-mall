package cn.huan.mall.product.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class MyThreadPoolConfig {

    @Value("${thread.pool.corePoolSize}")
    private int corePoolSize;

    @Value("${thread.pool.maximumPoolSize}")
    private int maximumPoolSize;

    @Value("${thread.pool.keepAliveTime}")
    private long keepAliveTime;

    @Value("${thread.pool.queueSize}")
    private int queueSize;


    @Bean
    public ThreadPoolExecutor threadPool() {
        return new ThreadPoolExecutor(corePoolSize,
                               maximumPoolSize,
                               keepAliveTime,
                               TimeUnit.SECONDS,
                               new LinkedBlockingQueue<>(queueSize),
                               Executors.defaultThreadFactory(),
                               new ThreadPoolExecutor.AbortPolicy());
    }
}
