package com.example.crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync // Background threads execution allow karne ke liye
public class AsyncConfig {

    @Bean(name = "crmAsyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5);       // Minimum active threads jo hamesha chalenge
        executor.setMaxPoolSize(20);       // Heavy load aane par maximum threads
        executor.setQueueCapacity(500);    // Queue me kitni requests hold ho sakti hain
        executor.setThreadNamePrefix("CRM-Async-Thread-");
        executor.initialize();

        return executor;
    }
}
