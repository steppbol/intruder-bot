package com.ffanaticism.intruder.telegramhandler.bot.impl;

import com.ffanaticism.intruder.telegramhandler.bot.BotContext;
import com.ffanaticism.intruder.telegramhandler.bot.TelegramBot;
import com.ffanaticism.intruder.telegramhandler.config.TelegramHandlerProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Component
@Profile("production")
public class WebhookBot extends SpringWebhookBot implements TelegramBot {
    private final TelegramHandlerProperty properties;
    private final BotContext botContext;

    @Autowired
    public WebhookBot(TelegramHandlerProperty properties, BotContext botContext) {
        super(SetWebhook.builder()
                .url(properties.getBotWebhookPath())
                .build());
        this.properties = properties;
        this.botContext = botContext;
    }

    @Override
    public String getBotUsername() {
        return properties.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return properties.getBotToken();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage()) {
            executeCommand(update);
        } else if (update.hasCallbackQuery()) {
            executeCallbackQuery(update);
        } else if (update.hasInlineQuery()) {
            executeInlineQuery(update);
        }
        return null;
    }

    @Override
    public String getBotPath() {
        return "";
    }

    @Override
    public BotApiMethod<?> onUpdate(Update update) {
        return onWebhookUpdateReceived(update);
    }

    @Override
    public Message sendMessage(SendMessage sendMessage) throws TelegramApiException {
        return execute(sendMessage);
    }

    @Override
    public void editMessage(EditMessageText editMessageText) throws TelegramApiException {
        execute(editMessageText);
    }

    @Override
    public void deleteMessage(DeleteMessage deleteMessage) throws TelegramApiException {
        execute(deleteMessage);
    }

    @Override
    public Message sendImage(SendPhoto sendPhoto) throws TelegramApiException {
        return execute(sendPhoto);
    }

    @Override
    public Message sendVideo(SendVideo sendVideo) throws TelegramApiException {
        return execute(sendVideo);
    }

    @Override
    public Message sendAudio(SendAudio sendAudio) throws TelegramApiException {
        return execute(sendAudio);
    }

    @Override
    public Message sendVoice(SendVoice sendVoice) throws TelegramApiException {
        return execute(sendVoice);
    }

    @Override
    public Message sendAnimation(SendAnimation sendAnimation) throws TelegramApiException {
        return execute(sendAnimation);
    }

    @Override
    public void editMessageReplyMarkup(EditMessageReplyMarkup editMessageReplyMarkup) throws TelegramApiException {
        execute(editMessageReplyMarkup);
    }

    @Override
    public void answerInlineQuery(AnswerInlineQuery answerInlineQuery) throws TelegramApiException {
        execute(answerInlineQuery);
    }

    private void executeCommand(Update update) {
        var message = update.getMessage();
        if (message != null) {
            botContext.handleCommand(message);
        }
    }

    private void executeCallbackQuery(Update update) {
        var callbackQuery = update.getCallbackQuery();
        if (callbackQuery != null) {
            botContext.handleCallbackQuery(callbackQuery);
        }
    }

    private void executeInlineQuery(Update update) {
        var inlineQuery = update.getInlineQuery();
        if (inlineQuery != null) {
            botContext.handleInlineQuery(inlineQuery);
        }
    }
}
