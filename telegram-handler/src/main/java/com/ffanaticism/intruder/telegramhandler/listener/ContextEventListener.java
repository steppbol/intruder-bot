package com.ffanaticism.intruder.telegramhandler.listener;

import com.ffanaticism.intruder.telegramhandler.service.BotService;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("production")
public class ContextEventListener {
    private final BotService botService;

    public ContextEventListener(BotService botService) {
        this.botService = botService;
    }

    @EventListener
    public void handleContextRefreshEvent(ContextRefreshedEvent contextRefreshedEvent) {
        botService.setWebhook();
    }
}
