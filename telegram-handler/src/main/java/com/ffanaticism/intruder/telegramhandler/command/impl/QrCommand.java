package com.ffanaticism.intruder.telegramhandler.command.impl;

import com.ffanaticism.intruder.serviceprovider.annotation.Registered;
import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.serviceprovider.service.ImageService;
import com.ffanaticism.intruder.telegramhandler.command.BaseCommand;
import com.ffanaticism.intruder.telegramhandler.entity.CommandType;
import com.ffanaticism.intruder.telegramhandler.service.BotService;
import com.ffanaticism.intruder.telegramhandler.util.BotUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class QrCommand implements BaseCommand {
    private final BotService botService;
    private final ImageService imageService;

    @Autowired
    public QrCommand(BotService botService, ImageService imageService) {
        this.botService = botService;
        this.imageService = imageService;
    }

    @Override
    @Registered
    public void execute(User user, Message message) {
        var text = BotUtil.fetchArgument(message.getText().trim().split(" "), 0);
        var chatId = message.getChatId().toString();
        var from = message.getFrom();
        botService.downloadImage(
                imageService.generateQr(text),
                image -> {
                    var sentMessage = botService.sendImage(image, chatId, message.getFrom(), getCommandType().toString());
                    image.deleteFile();
                    return sentMessage;
                },
                chatId,
                from,
                getCommandType().toString());
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.QR;
    }
}
