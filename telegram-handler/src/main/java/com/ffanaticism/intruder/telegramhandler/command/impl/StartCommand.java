package com.ffanaticism.intruder.telegramhandler.command.impl;

import com.ffanaticism.intruder.serviceprovider.annotation.Registered;
import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.telegramhandler.command.BaseCommand;
import com.ffanaticism.intruder.telegramhandler.entity.CommandType;
import com.ffanaticism.intruder.telegramhandler.service.BotService;
import com.ffanaticism.intruder.telegramhandler.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class StartCommand implements BaseCommand {
    private final BotService botService;
    private final MessageService messageService;

    @Autowired
    public StartCommand(BotService botService, MessageService messageService) {
        this.botService = botService;
        this.messageService = messageService;
    }

    @Override
    @Registered
    public void execute(User user, Message message) {
        botService.sendMessage(messageService.getText("reply.help_message"), message.getChatId().toString(),
                message.getFrom(), getCommandType().toString());
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.START;
    }
}
