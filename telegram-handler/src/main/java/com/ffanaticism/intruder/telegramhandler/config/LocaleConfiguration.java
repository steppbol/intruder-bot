package com.ffanaticism.intruder.telegramhandler.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
@ComponentScan(basePackages = "com.ffanaticism.intruder.telegramhandler")
public class LocaleConfiguration {
    @Bean
    public MessageSource messageSource() {
        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:locale/messages/messages", "classpath:locale/commands/commands", "classpath:locale/queries/queries");
        messageSource.setDefaultEncoding("UTF-8");

        return messageSource;
    }
}
