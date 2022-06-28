package com.ffanaticism.intruder.telegramhandler.bot;

import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface TelegramBot {
    BotApiMethod<?> onUpdate(Update update);

    Message sendMessage(SendMessage sendMessage) throws TelegramApiException;

    void editMessage(EditMessageText editMessageText) throws TelegramApiException;

    void deleteMessage(DeleteMessage deleteMessage) throws TelegramApiException;

    Message sendImage(SendPhoto sendPhoto) throws TelegramApiException;

    Message sendVideo(SendVideo sendVideo) throws TelegramApiException;

    Message sendAudio(SendAudio sendAudio) throws TelegramApiException;

    Message sendVoice(SendVoice sendVoice) throws TelegramApiException;

    Message sendAnimation(SendAnimation sendAnimation) throws TelegramApiException;

    void editMessageReplyMarkup(EditMessageReplyMarkup editMessageReplyMarkup) throws TelegramApiException;

    void answerInlineQuery(AnswerInlineQuery answerInlineQuery) throws TelegramApiException;
}
