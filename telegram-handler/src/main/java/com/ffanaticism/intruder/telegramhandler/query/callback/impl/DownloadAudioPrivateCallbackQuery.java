package com.ffanaticism.intruder.telegramhandler.query.callback.impl;

import com.ffanaticism.intruder.serviceprovider.annotation.Registered;
import com.ffanaticism.intruder.serviceprovider.entity.Audio;
import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.serviceprovider.service.AudioService;
import com.ffanaticism.intruder.serviceprovider.util.format.AudioFormat;
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
public class DownloadAudioPrivateCallbackQuery implements BaseCallbackQuery {
    private final BotService botService;
    private final AudioService audioService;
    private final MessageService messageService;

    @Autowired
    public DownloadAudioPrivateCallbackQuery(BotService botService, AudioService audioService, MessageService messageService) {
        this.botService = botService;
        this.audioService = audioService;
        this.messageService = messageService;
    }

    @Override
    @Registered
    public void execute(User user, CallbackQuery callbackQuery) {
        var url = BotUtil.getSubstringFromArgumentByRegex(callbackQuery.getData(), BotUtil.SCOPE_REGEX);
        var chatIdCharacteristic = user.getCharacteristic(UserCharacteristic.CHAT_ID);
        var from = callbackQuery.getFrom();
        var chatId = chatIdCharacteristic != null ? chatIdCharacteristic.getValue() : from.getId().toString();

        botService.downloadAudio(
                getFuture(url, chatId, from),
                audio -> botService.sendAudio(audio, chatId, from, getCallbackQueryType().toString(), null),
                chatId,
                from,
                getCallbackQueryType().toString());
    }

    @Override
    public CallbackQueryType getCallbackQueryType() {
        return CallbackQueryType.DOWNLOAD_AUDIO_PRIVATE;
    }

    private Function<Integer, CompletableFuture<Audio>> getFuture(String url, String chatId, org.telegram.telegrambots.meta.api.objects.User from) {
        return messageId -> audioService.download(url, AudioFormat.MP3_OUTPUT_FILE_FORMAT, "", 0, 0, progress -> {
            if (progress % BotUtil.DOWNLOAD_MESSAGE_UPDATE_RATE == 0) {
                botService.editMessage(messageService.getText("reply.start_downloading") + " " + progress + "%", chatId, messageId, from, getCallbackQueryType().toString());
            }
        });
    }
}