package com.ffanaticism.intruder.telegramhandler.service.impl;

import com.ffanaticism.intruder.telegramhandler.config.TelegramHandlerProperty;
import com.ffanaticism.intruder.telegramhandler.service.LocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class DefaultLocaleService implements LocaleService {
    private final Locale locale;
    private final MessageSource messageSource;

    @Autowired
    public DefaultLocaleService(MessageSource messageSource, TelegramHandlerProperty properties) {
        this.messageSource = messageSource;
        this.locale = Locale.forLanguageTag(properties.getLocaleTag());
    }

    @Override
    public String getMessage(String message) {
        return messageSource.getMessage(message, null, locale);
    }
}
