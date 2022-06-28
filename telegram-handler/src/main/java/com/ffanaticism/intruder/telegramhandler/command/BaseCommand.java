package com.ffanaticism.intruder.telegramhandler.command;

import com.ffanaticism.intruder.serviceprovider.annotation.Registered;
import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.telegramhandler.entity.CommandType;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface BaseCommand {
    @Registered
    void execute(User user, Message message);

    CommandType getCommandType();
}
