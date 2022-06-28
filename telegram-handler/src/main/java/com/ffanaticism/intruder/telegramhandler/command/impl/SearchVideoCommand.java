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
public class SearchVideoCommand implements BaseCommand {
    private final BotService botService;
    private final VideoService videoService;
    private final MessageService messageService;

    @Autowired
    public SearchVideoCommand(BotService botService, VideoService videoService, MessageService messageService) {
        this.botService = botService;
        this.videoService = videoService;
        this.messageService = messageService;
    }

    @Override
    @Registered
    public void execute(com.ffanaticism.intruder.serviceprovider.entity.User user, Message message) {
        var query = message.getText();
        if (!query.isBlank()) {
            var chatId = message.getChatId().toString();
            var from = message.getFrom();

            botService.searchVideo(
                    () -> videoService.search(query, 1),
                    video -> getFuture(video, chatId, from),
                    video -> botService.sendVideo(video, chatId, from, getCommandType().toString()),
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
        return CommandType.SEARCH_VIDEO;
    }

    private Function<Integer, CompletableFuture<Video>> getFuture(Video video, String chatId, User from) {
        return messageId -> videoService.download(video.getUrl(), VideoFormat.MP4_OUTPUT_FILE_FORMAT, 0, 0, progress -> {
            if (progress % BotUtil.DOWNLOAD_MESSAGE_UPDATE_RATE == 0) {
                botService.editMessage(messageService.getText("reply.start_downloading") + " " + progress + "%", chatId, messageId, from, getCommandType().toString());
            }
        });
    }
}
