package com.ffanaticism.intruder.telegramhandler.query.inline;

import com.ffanaticism.intruder.serviceprovider.annotation.Registered;
import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.telegramhandler.entity.InlineQueryType;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

public interface BaseInlineQuery {
    @Registered
    void execute(User user, InlineQuery inlineQuery);

    InlineQueryType getInlineQueryType();
}
