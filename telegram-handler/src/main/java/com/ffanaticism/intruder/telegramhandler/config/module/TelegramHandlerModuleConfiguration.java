package com.ffanaticism.intruder.telegramhandler.config.module;

import com.ffanaticism.intruder.serviceprovider.config.module.ServiceProviderConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ServiceProviderConfiguration
@ComponentScan(basePackages = {"com.ffanaticism.intruder.telegramhandler"})
public class TelegramHandlerModuleConfiguration {
}
