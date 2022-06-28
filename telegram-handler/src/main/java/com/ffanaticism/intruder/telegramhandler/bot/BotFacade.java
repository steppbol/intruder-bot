package com.ffanaticism.intruder.telegramhandler.bot;

import com.ffanaticism.intruder.telegramhandler.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class BotFacade {
    private final TelegramBot telegramBot;
    private final MessageService messageService;

    @Autowired
    public BotFacade(TelegramBot telegramBot, MessageService messageService) {
        this.telegramBot = telegramBot;
        this.messageService = messageService;
    }

    public BotApiMethod<?> onUpdate(Update update) {
        return telegramBot.onUpdate(update);
    }

    public Message sendMessage(SendMessage sendMessage, User user, String eventType) {
        Message message = null;
        try {
            log.info("Send message, chat id: {}, user: {}, user id: {}, event type: {}",
                    sendMessage.getChatId(), user.getUserName(), user.getId(), eventType);
            message = telegramBot.sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Sending message is failed, chat id: {}, user: {}, user id: {}, event type: {}",
                    sendMessage.getChatId(), user.getUserName(), user.getId(), eventType);
            e.printStackTrace();
        }

        return message;
    }

    public void editMessage(EditMessageText editMessageText, User user, String eventType) {
        try {
            log.info("Edit message, from chat id: {}, user: {}, user id: {}, event type: {}",
                    editMessageText.getChatId(), user.getUserName(), user.getId(), eventType);
            telegramBot.editMessage(editMessageText);
        } catch (TelegramApiException e) {
            log.error("Editing message is failed, from chat id: {}, user: {}, user id: {}, event type: {}",
                    editMessageText.getChatId(), user.getUserName(), user.getId(), eventType);
            e.printStackTrace();

            var sendMessage = new SendMessage();
            sendMessage.setChatId(editMessageText.getChatId());
            sendMessage.setText(messageService.getText("exception.any_exception"));
            sendMessage(sendMessage, user, eventType);
        }
    }

    public void deleteMessage(DeleteMessage deleteMessage, User user, String eventType) {
        try {
            log.info("Delete message, from chat id: {}, user: {}, user id: {}, event type: {}",
                    deleteMessage.getChatId(), user.getUserName(), user.getId(), eventType);
            telegramBot.deleteMessage(deleteMessage);
        } catch (TelegramApiException e) {
            log.error("Deleting message is failed, from chat id: {}, user: {}, user id: {}, event type: {}",
                    deleteMessage.getChatId(), user.getUserName(), user.getId(), eventType);
            e.printStackTrace();

            var sendMessage = new SendMessage();
            sendMessage.setChatId(deleteMessage.getChatId());
            sendMessage.setText(messageService.getText("exception.any_exception"));
            sendMessage(sendMessage, user, eventType);
        }
    }

    public Message sendImage(SendPhoto sendPhoto, User user, String eventType) {
        Message message = null;
        try {
            log.info("Send image, chat id: {}, user: {}, user id: {}, event type: {}",
                    sendPhoto.getChatId(), user.getUserName(), user.getId(), eventType);
            message = telegramBot.sendImage(sendPhoto);
        } catch (TelegramApiException e) {
            log.error("Sending image is failed, chat id: {}, user: {}, user id: {}, event type: {}",
                    sendPhoto.getChatId(), user.getUserName(), user.getId(), eventType);
            e.printStackTrace();

            var sendMessage = new SendMessage();
            sendMessage.setChatId(sendPhoto.getChatId());
            sendMessage.setText(messageService.getText("exception.any_exception"));
            sendMessage(sendMessage, user, eventType);
        }
        return message;
    }

    public Message sendVideo(SendVideo sendVideo, User user, String eventType) {
        Message message = null;
        try {
            log.info("Send video, chat id: {}, user: {}, user id: {}, event type: {}",
                    sendVideo.getChatId(), user.getUserName(), user.getId(), eventType);
            message = telegramBot.sendVideo(sendVideo);
        } catch (TelegramApiException e) {
            log.error("Sending video is failed, chat id: {}, user: {}, user id: {}, event type: {}",
                    sendVideo.getChatId(), user.getUserName(), user.getId(), eventType);
            e.printStackTrace();

            var sendMessage = new SendMessage();
            sendMessage.setChatId(sendVideo.getChatId());
            sendMessage.setText(messageService.getText("exception.any_exception"));
            sendMessage(sendMessage, user, eventType);
        }

        return message;
    }

    public Message sendAudio(SendAudio sendAudio, User user, String eventType) {
        Message message = null;
        try {
            log.info("Send audio, chat id: {}, user: {}, user id: {}, event type: {}",
                    sendAudio.getChatId(), user.getUserName(), user.getId(), eventType);
            message = telegramBot.sendAudio(sendAudio);
        } catch (TelegramApiException e) {
            log.error("Sending audio is failed, chat id: {}, user: {}, user id: {}, event type: {}",
                    sendAudio.getChatId(), user.getUserName(), user.getId(), eventType);
            e.printStackTrace();

            var sendMessage = new SendMessage();
            sendMessage.setChatId(sendAudio.getChatId());
            sendMessage.setText(messageService.getText("exception.any_exception"));
            sendMessage(sendMessage, user, eventType);
        }
        return message;
    }

    public Message sendVoice(SendVoice sendVoice, User user, String eventType) {
        Message message = null;
        try {
            log.info("Send voice, chat id: {}, user: {}, user id: {}, event type: {}", sendVoice.getChatId(),
                    user.getUserName(), user.getId(), eventType);
            message = telegramBot.sendVoice(sendVoice);
        } catch (TelegramApiException e) {
            log.error("Sending voice is failed, chat id: {}, user: {}, user id: {}, event type: {}",
                    sendVoice.getChatId(), user.getUserName(), user.getId(), eventType);
            e.printStackTrace();

            var sendMessage = new SendMessage();
            sendMessage.setChatId(sendVoice.getChatId());
            sendMessage.setText(messageService.getText("exception.any_exception"));
            sendMessage(sendMessage, user, eventType);
        }
        return message;
    }

    public Message sendAnimation(SendAnimation sendAnimation, User user, String eventType) {
        Message message = null;
        try {
            log.info("Send animation, chat id: {}, user: {}, user id: {}, event type: {}",
                    sendAnimation.getChatId(), user.getUserName(), user.getId(), eventType);
            message = telegramBot.sendAnimation(sendAnimation);
        } catch (TelegramApiException e) {
            log.error("Sending animation is failed, chat id: {}, user: {}, user id: {}, event type: {}",
                    sendAnimation.getChatId(), user.getUserName(), user.getId(), eventType);
            e.printStackTrace();

            var sendMessage = new SendMessage();
            sendMessage.setChatId(sendAnimation.getChatId());
            sendMessage.setText(messageService.getText("exception.any_exception"));
            sendMessage(sendMessage, user, eventType);
        }
        return message;
    }

    public void editMessageReplyMarkup(EditMessageReplyMarkup editMessageReplyMarkup, User user, String eventType) {
        try {
            log.info("Edit message reply markup, chat id: {}, user: {}, user id: {}, event type: {}",
                    editMessageReplyMarkup.getChatId(), user.getUserName(), user.getId(), eventType);
            telegramBot.editMessageReplyMarkup(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            log.error("Editing message reply markup is failed, chat id: {}, user: {}, user id: {}, event type: {}",
                    editMessageReplyMarkup.getChatId(), user.getUserName(), user.getId(), eventType);
            e.printStackTrace();

            var sendMessage = new SendMessage();
            sendMessage.setChatId(editMessageReplyMarkup.getChatId());
            sendMessage.setText(messageService.getText("exception.any_exception"));
            sendMessage(sendMessage, user, eventType);
        }
    }

    public void answerInlineQuery(AnswerInlineQuery answerInlineQuery, User user, String eventType) {
        try {
            log.info("Answer inline query, user: {}, user id: {}, event type: {}",
                    user.getUserName(), user.getId(), eventType);
            telegramBot.answerInlineQuery(answerInlineQuery);
        } catch (TelegramApiException e) {
            log.error("Answering inline query is failed, user: {}, user id: {}, event type: {}",
                    user.getUserName(), user.getId(), eventType);
            e.printStackTrace();
        }
    }
}
