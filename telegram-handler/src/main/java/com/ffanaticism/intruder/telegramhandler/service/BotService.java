package com.ffanaticism.intruder.telegramhandler.service;

import com.ffanaticism.intruder.serviceprovider.entity.Audio;
import com.ffanaticism.intruder.serviceprovider.entity.FileEntity;
import com.ffanaticism.intruder.serviceprovider.entity.Image;
import com.ffanaticism.intruder.serviceprovider.entity.StreamedEntity;
import com.ffanaticism.intruder.serviceprovider.entity.Video;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public interface BotService {
    BotApiMethod<?> onUpdate(Update update);

    void setWebhook();

    Message sendMessage(String text, String chatId, User from, String eventType);

    void editMessage(String text, String chatId, Integer messageId, User from, String eventType);

    void deleteMessage(String chatId, Integer messageId, User from, String eventType);

    Message sendImage(StreamedEntity image, String chatId, User from, String eventType);

    Message sendVideo(StreamedEntity video, String chatId, User from, String eventType);

    Message sendAudio(StreamedEntity audio, String chatId, User from, String eventType, ReplyKeyboard replyKeyboard);

    void sendAudio(String fileId, List<MessageEntity> messageEntities, String caption, User from, String eventType);

    Message sendVoice(StreamedEntity audio, String chatId, User from, String eventType);

    Message sendAnimation(StreamedEntity animation, String chatId, User from, String eventType);

    void editMessageReplyMarkup(String chatId, Integer messageId, String inlineMessageId, InlineKeyboardMarkup inlineKeyboardMarkup, User from, String eventType);

    void downloadVideo(Function<Integer, CompletableFuture<Video>> downloadFunction, Function<FileEntity, Message> sendFunction, String chatId, User from, String eventType);

    void downloadAudio(Function<Integer, CompletableFuture<Audio>> downloadFunction, Function<FileEntity, Message> sendFunction, String chatId, User from, String eventType);

    void downloadImage(CompletableFuture<Image> downloadFuture, Function<FileEntity, Message> sendFunction, String chatId, User from, String eventType);

    void searchVideo(Supplier<CompletableFuture<List<Video>>> searchSupplier, Function<Video, Function<Integer, CompletableFuture<Video>>> downloadFunction, Function<FileEntity, Message> sendFunction, String chatId, User from, String eventType);

    void searchAudio(Supplier<CompletableFuture<List<Video>>> searchSupplier, Function<Video, Function<Integer, CompletableFuture<Audio>>> downloadFunction, Function<FileEntity, Message> sendFunction, String chatId, User from, String eventType);

    void answerInlineQuery(AnswerInlineQuery answerInlineQuery, User from, String eventType);

    void sendSearchAudioAnswerInlineQuery(String queryId, List<Video> video, int offset, int resultSize, User from, String eventType);

    void sendSearchVideoAnswerInlineQuery(String queryId, List<Video> video, int offset, int resultSize, User from, String eventType);
}
