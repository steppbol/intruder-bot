package com.ffanaticism.intruder.telegramhandler.controller.v1;

import com.ffanaticism.intruder.telegramhandler.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.ffanaticism.intruder.telegramhandler.controller.ApiPath.API_PATH;
import static com.ffanaticism.intruder.telegramhandler.controller.ApiPath.EVENTS_PATH;
import static com.ffanaticism.intruder.telegramhandler.controller.ApiPath.TELEGRAM_HANDLER_PATH;
import static com.ffanaticism.intruder.telegramhandler.controller.ApiPath.V1_PATH;

@Profile("production")
@RestController
@RequestMapping(value = API_PATH + V1_PATH + TELEGRAM_HANDLER_PATH + EVENTS_PATH)
public class EventController {
    private final BotService botService;

    @Autowired
    public EventController(BotService botService) {
        this.botService = botService;
    }

    @PostMapping(value = "/callback")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return botService.onUpdate(update);
    }
}
