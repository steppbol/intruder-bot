package com.ffanaticism.intruder;

import com.ffanaticism.intruder.telegramhandler.config.module.TelegramHandlerConfiguration;
import com.ffanaticism.intruder.webhandler.config.module.WebHandlerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@PropertySources({
        @PropertySource("classpath:telegram-handler.yml"),
        @PropertySource("classpath:web-handler.yml"),
        @PropertySource("classpath:service-provider.yml"),
})
@SpringBootApplication
@TelegramHandlerConfiguration
@WebHandlerConfiguration
public class IntruderApplication {
    public static void main(String[] args) {
        SpringApplication.run(IntruderApplication.class, args);
    }
}
