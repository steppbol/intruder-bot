package com.ffanaticism.intruder.telegramhandler.command.impl;

import com.ffanaticism.intruder.serviceprovider.annotation.Registered;
import com.ffanaticism.intruder.serviceprovider.entity.Video;
import com.ffanaticism.intruder.serviceprovider.service.VideoService;
import com.ffanaticism.intruder.serviceprovider.util.format.VideoFormat;
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
public class VideoCommand implements BaseCommand {
    private final BotService botService;
    private final VideoService videoService;
    private final MessageService messageService;

    @Autowired
    public VideoCommand(BotService botService, VideoService videoService, MessageService messageService) {
        this.botService = botService;
        this.videoService = videoService;
        this.messageService = messageService;
    }

    @Override
    @Registered
    public void execute(com.ffanaticism.intruder.serviceprovider.entity.User user, Message message) {
        var messageText = message.getText();
        var text = messageText.trim().split(" ");
        var url = BotUtil.fetchArgument(text, 0);

        if (!url.isBlank()) {
            var chatId = message.getChatId().toString();
            var from = message.getFrom();
            var result = BotUtil.getOffsetAndDuration(text, 1, 2);
            botService.downloadVideo(
                    getFuture(url, result.offset(), result.duration(), chatId, from),
                    video -> botService.sendVideo(video, chatId, from, getCommandType().toString()),
                    chatId,
                    from,
                    getCommandType().toString());
        } else {
            botService.sendMessage(messageService.getText("reply.invalid_arguments"), message.getChatId().toString(), message.getFrom(), getCommandType().toString());
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.VIDEO;
    }

    private Function<Integer, CompletableFuture<Video>> getFuture(String url, float offset, float duration, String chatId, User from) {
        return messageId -> videoService.download(url, VideoFormat.MP4_OUTPUT_FILE_FORMAT, offset, duration, progress -> {
            if (progress % BotUtil.DOWNLOAD_MESSAGE_UPDATE_RATE == 0) {
                botService.editMessage(messageService.getText("reply.start_downloading") + " " + progress + "%", chatId, messageId, from, getCommandType().toString());
            }
        });
    }
}
