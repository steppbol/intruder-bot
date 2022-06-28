package com.ffanaticism.intruder.telegramhandler.service;

import com.ffanaticism.intruder.telegramhandler.entity.InlineQueryType;

public interface InlineQueryService {
    InlineQueryType getInlineQueryType(String query);
}
