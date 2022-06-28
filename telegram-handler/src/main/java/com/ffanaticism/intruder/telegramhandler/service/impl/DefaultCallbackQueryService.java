package com.ffanaticism.intruder.telegramhandler.service.impl;

import com.ffanaticism.intruder.telegramhandler.entity.CallbackQueryType;
import com.ffanaticism.intruder.telegramhandler.service.CallbackQueryService;
import com.ffanaticism.intruder.telegramhandler.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DefaultCallbackQueryService implements CallbackQueryService {
    private final Map<String, CallbackQueryType> callbackQueries = new HashMap<>();

    @Autowired
    public DefaultCallbackQueryService(MessageService messageService) {
        callbackQueries.put(messageService.getText("callback_query.send_audio_channel"), CallbackQueryType.SEND_AUDIO_CHANNEL);
        callbackQueries.put(messageService.getText("callback_query.download_audio_private"), CallbackQueryType.DOWNLOAD_AUDIO_PRIVATE);
        callbackQueries.put(messageService.getText("callback_query.download_video_private"), CallbackQueryType.DOWNLOAD_VIDEO_PRIVATE);
    }

    @Override
    public CallbackQueryType getCallbackQueryType(String data) {
        return callbackQueries.keySet().stream()
                .filter(data::contains)
                .findFirst().map(callbackQueries::get)
                .orElse(null);
    }
}
