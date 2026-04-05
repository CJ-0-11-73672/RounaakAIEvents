package com.AITrial.AIEventsTry.AppConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.Executor;

@Configuration
public class AppConfig {
@Bean
public RestTemplate restTemplate(){
    return new RestTemplate();
}
@Bean("taskExecutor")
public Executor taskExecutor(){
ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
executor.setCorePoolSize(10);
executor.setMaxPoolSize(50);
executor.setQueueCapacity(100);
executor.setThreadNamePrefix("AI-Thread");
executor.initialize();
return executor;
}
}
