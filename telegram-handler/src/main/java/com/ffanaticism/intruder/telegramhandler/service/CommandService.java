package com.ffanaticism.intruder.telegramhandler.service;

import com.ffanaticism.intruder.telegramhandler.entity.CommandType;

public interface CommandService {
    Command getCommandType(String commandText);

    record Command(String name, CommandType type) {
    }
}

