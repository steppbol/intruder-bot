package com.ffanaticism.intruder.telegramhandler.service.impl;

import com.ffanaticism.intruder.telegramhandler.config.TelegramHandlerProperty;
import com.ffanaticism.intruder.telegramhandler.entity.CommandType;
import com.ffanaticism.intruder.telegramhandler.service.CommandService;
import com.ffanaticism.intruder.telegramhandler.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DefaultCommandService implements CommandService {
    private final Map<String, CommandType> commands = new HashMap<>();
    private final TelegramHandlerProperty properties;

    @Autowired
    public DefaultCommandService(MessageService messageService, TelegramHandlerProperty properties) {
        this.properties = properties;

        registerCommand(messageService.getText("command.start"), CommandType.START);
        registerCommand(messageService.getText("command.help"), CommandType.HELP);
        registerCommand(messageService.getText("command.platform"), CommandType.PLATFORM);
        registerCommand(messageService.getText("command.contact"), CommandType.CONTACT);
        registerCommand(messageService.getText("command.image"), CommandType.PHOTO);
        registerCommand(messageService.getText("command.video"), CommandType.VIDEO);
        registerCommand(messageService.getText("command.audio"), CommandType.AUDIO);
        registerCommand(messageService.getText("command.voice"), CommandType.VOICE);
        registerCommand(messageService.getText("command.qr"), CommandType.QR);
        registerCommand(messageService.getText("command.gif"), CommandType.GIF);
        registerCommand(messageService.getText("command.search_video"), CommandType.SEARCH_VIDEO);
        registerCommand(messageService.getText("command.search_audio"), CommandType.SEARCH_AUDIO);
    }

    @Override
    public Command getCommandType(String commandText) {
        CommandType commandType = null;
        var command = "";
        try {
            var splitText = commandText.toLowerCase().trim().split(" ");

            if (splitText.length >= 2) {
                command = splitText[0] + " " + splitText[1];
                commandType = commands.get(command);
            }

            if (commandType == null && splitText.length >= 1) {
                command = splitText[0];
                commandType = commands.get(command);
            }
        } catch (Exception e) {
            log.error("Getting command type is failed, message: {}", commandText);
            e.printStackTrace();
        }

        return new Command(command, commandType);
    }

    private void registerCommand(String command, CommandType commandType) {
        var split = command.trim().split(",");

        Arrays.stream(split).forEach(e -> {
            commands.put(e, commandType);
            commands.put(e + "@" + properties.getBotUsername(), commandType);
        });
    }
}