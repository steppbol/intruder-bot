package com.ffanaticism.intruder.serviceprovider.config.module;

import com.ffanaticism.intruder.serviceprovider.config.ServiceProviderProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.ffanaticism.intruder.serviceprovider"})
@ComponentScan(basePackages = {"com.ffanaticism.intruder.serviceprovider"})
public class ServiceProviderModuleConfiguration {
    private final ServiceProviderProperty properties;

    @Autowired
    public ServiceProviderModuleConfiguration(ServiceProviderProperty properties) {
        this.properties = properties;
    }

    @Bean(name = "servicePool")
    public Executor asyncExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getDownloadPoolCoreSize());
        executor.setMaxPoolSize(properties.getDownloadPoolMaxSize());
        executor.setQueueCapacity(properties.getDownloadPoolQueueCapacity());
        executor.setThreadNamePrefix("service-pool-");
        executor.initialize();
        return executor;
    }
}
