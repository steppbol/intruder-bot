package com.ffanaticism.intruder.telegramhandler.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Getter
@Configuration
@PropertySource(value = "classpath:telegram-handler.yml", factory = YamlPropertySourceFactory.class)
public class TelegramHandlerProperty {
    private final String telegramApiBotUrl = "https://api.telegram.org/bot";
    private final String telegramUrl = "https://t.me/";
    @Value("${bot.username}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.webhook.path}")
    private String botWebhookPath;
    @Value("${bot.channel-id}")
    private String botChannelId;
    @Value("${bot.administrators}")
    private List<String> botAdministrators;
    @Value("${application.locale.tag}")
    private String localeTag;
}
