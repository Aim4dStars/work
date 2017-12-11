package com.bt.nextgen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.bt.nextgen.service.bassil.BassilServiceImpl;
import com.bt.nextgen.service.bassil.StatementService;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ServiceConfig implements AsyncConfigurer {

    @Bean
    @Primary
    public StatementService produceStatementService() {
        return new BassilServiceImpl();
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("AsyncTaskExecutor-");
        executor.initialize();
        return executor;
    }
}
