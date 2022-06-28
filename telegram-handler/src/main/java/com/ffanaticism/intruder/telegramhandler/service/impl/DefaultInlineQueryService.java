package com.ffanaticism.intruder.telegramhandler.service.impl;

import com.ffanaticism.intruder.telegramhandler.entity.InlineQueryType;
import com.ffanaticism.intruder.telegramhandler.service.InlineQueryService;
import com.ffanaticism.intruder.telegramhandler.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DefaultInlineQueryService implements InlineQueryService {
    private final Map<String, InlineQueryType> inlineQueries = new HashMap<>();
    private final MessageService messageService;

    @Autowired
    public DefaultInlineQueryService(MessageService messageService) {
        this.messageService = messageService;
        inlineQueries.put(this.messageService.getText("inline_query.empty"), InlineQueryType.EMPTY);
        inlineQueries.put(this.messageService.getText("inline_query.search_audio"), InlineQueryType.SEARCH_AUDIO);
        inlineQueries.put(this.messageService.getText("inline_query.search_video"), InlineQueryType.SEARCH_VIDEO);
    }

    @Override
    public InlineQueryType getInlineQueryType(String query) {
        InlineQueryType inlineQueryType = null;
        var inlineQuery = query.toLowerCase().trim().split(" ");
        if (inlineQuery.length >= 1) {
            if (!inlineQuery[0].isBlank()) {
                inlineQueryType = inlineQueries.get(inlineQuery[0]);
            } else {
                inlineQueryType = inlineQueries.get(messageService.getText("inline_query.empty"));
            }
        }

        return inlineQueryType;
    }
}
