package com.ffanaticism.intruder.telegramhandler.query.callback;

import com.ffanaticism.intruder.serviceprovider.annotation.Registered;
import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.telegramhandler.entity.CallbackQueryType;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface BaseCallbackQuery {
    @Registered
    void execute(User user, CallbackQuery callbackQuery);

    CallbackQueryType getCallbackQueryType();
}
