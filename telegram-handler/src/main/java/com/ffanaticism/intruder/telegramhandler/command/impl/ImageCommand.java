package com.ffanaticism.intruder.telegramhandler.command.impl;

import com.ffanaticism.intruder.serviceprovider.annotation.Registered;
import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.serviceprovider.service.ImageService;
import com.ffanaticism.intruder.telegramhandler.command.BaseCommand;
import com.ffanaticism.intruder.telegramhandler.entity.CommandType;
import com.ffanaticism.intruder.telegramhandler.service.BotService;
import com.ffanaticism.intruder.telegramhandler.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;

@Component
public class ImageCommand implements BaseCommand {
    private final BotService botService;
    private final ImageService imageService;
    private final MessageService messageService;

    @Autowired
    public ImageCommand(BotService botService, ImageService imageService, MessageService messageService) {
        this.botService = botService;
        this.imageService = imageService;
        this.messageService = messageService;
    }

    @Override
    @Registered
    public void execute(User user, Message message) {
        var chatId = message.getChatId().toString();
        var from = message.getFrom();
        var urls = Arrays.asList(message.getText().trim().split(" "));
        if (!urls.isEmpty()) {
            for (var url : urls) {
                botService.downloadImage(
                        imageService.download(url),
                        image -> botService.sendImage(image, chatId, message.getFrom(), getCommandType().toString()),
                        chatId,
                        from,
                        getCommandType().toString());
            }
        } else {
            botService.sendMessage(messageService.getText("reply.invalid_arguments"), chatId, from, getCommandType().toString());
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.PHOTO;
    }
}
