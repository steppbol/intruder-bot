package com.ffanaticism.intruder.webhandler.config.module;

import com.ffanaticism.intruder.serviceprovider.config.module.ServiceProviderConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ServiceProviderConfiguration
@ComponentScan(basePackages = {"com.ffanaticism.intruder.webhandler"})
public class WebHandlerModuleConfiguration {
}
