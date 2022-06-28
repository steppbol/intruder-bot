package com.ffanaticism.intruder.telegramhandler.command.impl;

import com.ffanaticism.intruder.serviceprovider.annotation.Registered;
import com.ffanaticism.intruder.serviceprovider.entity.Audio;
import com.ffanaticism.intruder.serviceprovider.service.AudioService;
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
public class VoiceCommand implements BaseCommand {
    private final BotService botService;
    private final AudioService audioService;
    private final MessageService messageService;

    @Autowired
    public VoiceCommand(BotService botService, AudioService audioService, MessageService messageService) {
        this.botService = botService;
        this.audioService = audioService;
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

            botService.downloadAudio(
                    getFuture(url, result.offset(), result.duration(), chatId, from),
                    audio -> botService.sendVoice(audio, chatId, from, getCommandType().toString()),
                    chatId,
                    from,
                    getCommandType().toString());
        } else {
            botService.sendMessage(messageService.getText("reply.invalid_arguments"), message.getChatId().toString(), message.getFrom(), getCommandType().toString());
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.VOICE;
    }

    private Function<Integer, CompletableFuture<Audio>> getFuture(String url, float offset, float duration, String chatId, User from) {
        return messageId -> audioService.download(url, AudioFormat.OGG_OUTPUT_FILE_FORMAT, "", offset, duration, progress -> {
            if (progress % BotUtil.DOWNLOAD_MESSAGE_UPDATE_RATE == 0) {
                botService.editMessage(messageService.getText("reply.start_downloading") + " " + progress + "%", chatId, messageId, from, getCommandType().toString());
            }
        });
    }
}
