package com.ffanaticism.intruder.telegramhandler.command.impl;

import com.ffanaticism.intruder.serviceprovider.annotation.Registered;
import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.serviceprovider.util.PlatformUtil;
import com.ffanaticism.intruder.telegramhandler.command.BaseCommand;
import com.ffanaticism.intruder.telegramhandler.entity.CommandType;
import com.ffanaticism.intruder.telegramhandler.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class PlatformCommand implements BaseCommand {
    private final BotService botService;

    @Autowired
    public PlatformCommand(BotService botService) {
        this.botService = botService;
    }

    @Override
    @Registered
    public void execute(User user, Message message) {
        botService.sendMessage(String.join("\n", PlatformUtil.getAll()), message.getChatId().toString(),
                message.getFrom(), getCommandType().toString());
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.PLATFORM;
    }
}
