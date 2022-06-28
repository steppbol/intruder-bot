package com.ffanaticism.intruder.telegramhandler.query.callback.impl;

import com.ffanaticism.intruder.serviceprovider.annotation.Registered;
import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.telegramhandler.config.TelegramHandlerProperty;
import com.ffanaticism.intruder.telegramhandler.entity.CallbackQueryType;
import com.ffanaticism.intruder.telegramhandler.query.callback.BaseCallbackQuery;
import com.ffanaticism.intruder.telegramhandler.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class SendAudioChannelCallbackQuery implements BaseCallbackQuery {
    private final BotService botService;
    private final TelegramHandlerProperty properties;

    @Autowired
    public SendAudioChannelCallbackQuery(BotService botService, TelegramHandlerProperty properties) {
        this.botService = botService;
        this.properties = properties;
    }

    @Override
    @Registered
    public void execute(User user, CallbackQuery callbackQuery) {
        var from = callbackQuery.getFrom();

        String sendBy;
        if (properties.getBotAdministrators().contains(from.getUserName())) {
            sendBy = properties.getBotUsername();
        } else {
            sendBy = from.getUserName();
        }

        var messageEntities = MessageEntity.builder()
                .type(EntityType.TEXTLINK)
                .user(callbackQuery.getFrom())
                .length(4)
                .offset(3)
                .url(properties.getTelegramUrl() + sendBy)
                .build();

        var message = callbackQuery.getMessage();
        var callbackQueryType = getCallbackQueryType().toString();
        botService.sendAudio(message.getAudio().getFileId(), List.of(messageEntities), "by user", from, callbackQueryType);

        var updatedMarkupInline = new InlineKeyboardMarkup();
        var rowsInline = new ArrayList<List<InlineKeyboardButton>>();
        rowsInline.add(new ArrayList<>());
        updatedMarkupInline.setKeyboard(rowsInline);

        botService.editMessageReplyMarkup(message.getChatId().toString(),
                message.getMessageId(), callbackQuery.getInlineMessageId(), updatedMarkupInline, from, callbackQueryType);
    }

    @Override
    public CallbackQueryType getCallbackQueryType() {
        return CallbackQueryType.SEND_AUDIO_CHANNEL;
    }
}
