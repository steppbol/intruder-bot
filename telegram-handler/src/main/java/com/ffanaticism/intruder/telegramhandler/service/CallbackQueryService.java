package com.ffanaticism.intruder.telegramhandler.service;

import com.ffanaticism.intruder.telegramhandler.entity.CallbackQueryType;

public interface CallbackQueryService {
    CallbackQueryType getCallbackQueryType(String data);
}
