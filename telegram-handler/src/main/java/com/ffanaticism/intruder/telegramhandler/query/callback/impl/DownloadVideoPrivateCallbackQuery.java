package com.ffanaticism.intruder.telegramhandler.query.callback.impl;

import com.ffanaticism.intruder.serviceprovider.annotation.Registered;
import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.serviceprovider.entity.Video;
import com.ffanaticism.intruder.serviceprovider.service.VideoService;
import com.ffanaticism.intruder.serviceprovider.util.format.VideoFormat;
import com.ffanaticism.intruder.telegramhandler.entity.CallbackQueryType;
import com.ffanaticism.intruder.telegramhandler.query.callback.BaseCallbackQuery;
import com.ffanaticism.intruder.telegramhandler.service.BotService;
import com.ffanaticism.intruder.telegramhandler.service.MessageService;
import com.ffanaticism.intruder.telegramhandler.util.BotUtil;
import com.ffanaticism.intruder.telegramhandler.util.UserCharacteristic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Component
public class DownloadVideoPrivateCallbackQuery implements BaseCallbackQuery {
    private final BotService botService;
    private final VideoService videoService;
    private final MessageService messageService;

    @Autowired
    public DownloadVideoPrivateCallbackQuery(BotService botService, VideoService videoService, MessageService messageService) {
        this.botService = botService;
        this.videoService = videoService;
        this.messageService = messageService;
    }

    @Override
    @Registered
    public void execute(User user, CallbackQuery callbackQuery) {
        var url = BotUtil.getSubstringFromArgumentByRegex(callbackQuery.getData(), BotUtil.SCOPE_REGEX);
        var chatIdCharacteristic = user.getCharacteristic(UserCharacteristic.CHAT_ID);
        var from = callbackQuery.getFrom();
        var chatId = chatIdCharacteristic != null ? chatIdCharacteristic.getValue() : from.getId().toString();

        botService.downloadVideo(
                getFuture(url, chatId, from),
                video -> botService.sendVideo(video, chatId, from, getCallbackQueryType().toString()),
                chatId,
                from,
                getCallbackQueryType().toString());
    }

    @Override
    public CallbackQueryType getCallbackQueryType() {
        return CallbackQueryType.DOWNLOAD_VIDEO_PRIVATE;
    }

    private Function<Integer, CompletableFuture<Video>> getFuture(String url, String chatId, org.telegram.telegrambots.meta.api.objects.User from) {
        return messageId -> videoService.download(url, VideoFormat.MP4_OUTPUT_FILE_FORMAT, 0, 0, progress -> {
            if (progress % BotUtil.DOWNLOAD_MESSAGE_UPDATE_RATE == 0) {
                botService.editMessage(messageService.getText("reply.start_downloading") + " " + progress + "%", chatId, messageId, from, getCallbackQueryType().toString());
            }
        });
    }
}
