package com.ffanaticism.intruder.telegramhandler.command.impl;

import com.ffanaticism.intruder.serviceprovider.annotation.Registered;
import com.ffanaticism.intruder.serviceprovider.entity.Audio;
import com.ffanaticism.intruder.serviceprovider.entity.Video;
import com.ffanaticism.intruder.serviceprovider.service.AudioService;
import com.ffanaticism.intruder.serviceprovider.service.VideoService;
import com.ffanaticism.intruder.serviceprovider.util.format.AudioFormat;
import com.ffanaticism.intruder.telegramhandler.command.BaseCommand;
import com.ffanaticism.intruder.telegramhandler.entity.CommandType;
import com.ffanaticism.intruder.telegramhandler.service.BotService;
import com.ffanaticism.intruder.telegramhandler.service.MessageService;
import com.ffanaticism.intruder.telegramhandler.util.BotUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Component
public class SearchAudioCommand implements BaseCommand {
    private final BotService botService;
    private final VideoService videoService;
    private final AudioService audioService;
    private final MessageService messageService;

    @Autowired
    public SearchAudioCommand(BotService botService, VideoService videoService, AudioService audioService, MessageService messageService) {
        this.botService = botService;
        this.videoService = videoService;
        this.audioService = audioService;
        this.messageService = messageService;
    }

    @Override
    @Registered
    public void execute(com.ffanaticism.intruder.serviceprovider.entity.User user, Message message) {
        var query = message.getText();
        if (!query.isBlank()) {
            var chatId = message.getChatId().toString();
            var from = message.getFrom();
            var markupInline = BotUtil.buildReplyMarkup(messageService.getText("button.send_channel"), messageService.getText("callback_query.send_audio_channel"));

            botService.searchAudio(
                    () -> videoService.search(query, 1),
                    video -> getFuture(video, chatId, from),
                    audio -> botService.sendAudio(audio, chatId, from, getCommandType().toString(), markupInline),
                    chatId,
                    from,
                    getCommandType().toString()
            );
        } else {
            botService.sendMessage(messageService.getText("reply.empty_query"),
                    message.getChatId().toString(), message.getFrom(), getCommandType().toString());
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.SEARCH_AUDIO;
    }

    private Function<Integer, CompletableFuture<Audio>> getFuture(Video video, String chatId, User from) {
        return messageId -> audioService.download(video.getUrl(), AudioFormat.MP3_OUTPUT_FILE_FORMAT, "", 0, 0, progress -> {
            if (progress % BotUtil.DOWNLOAD_MESSAGE_UPDATE_RATE == 0) {
                botService.editMessage(messageService.getText("reply.start_downloading") + " " + progress + "%", chatId, messageId, from, getCommandType().toString());
            }
        });
    }
}
