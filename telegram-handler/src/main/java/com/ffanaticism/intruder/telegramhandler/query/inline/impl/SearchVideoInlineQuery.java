package com.ffanaticism.intruder.telegramhandler.query.inline.impl;

import com.ffanaticism.intruder.serviceprovider.annotation.Registered;
import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.serviceprovider.service.VideoService;
import com.ffanaticism.intruder.telegramhandler.entity.InlineQueryType;
import com.ffanaticism.intruder.telegramhandler.query.inline.BaseInlineQuery;
import com.ffanaticism.intruder.telegramhandler.service.BotService;
import com.ffanaticism.intruder.telegramhandler.util.BotUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

@Component
public class SearchVideoInlineQuery implements BaseInlineQuery {
    private static final int RESULT_SIZE = 10;

    private final VideoService videoService;
    private final BotService botService;

    @Autowired
    public SearchVideoInlineQuery(VideoService videoService, BotService botService) {
        this.videoService = videoService;
        this.botService = botService;
    }

    @Override
    @Registered
    public void execute(User user, InlineQuery inlineQuery) {
        var query = BotUtil.fetchArgument(inlineQuery.getQuery().trim().split(" ", 2), 1);
        var offset = inlineQuery.getOffset().isBlank() ? RESULT_SIZE : Integer.parseInt(inlineQuery.getOffset());
        videoService.search(query, offset)
                .thenAccept(results -> botService.sendSearchVideoAnswerInlineQuery(
                        inlineQuery.getId(),
                        results,
                        offset,
                        RESULT_SIZE,
                        inlineQuery.getFrom(),
                        getInlineQueryType().toString()));
    }

    @Override
    public InlineQueryType getInlineQueryType() {
        return InlineQueryType.SEARCH_VIDEO;
    }
}
