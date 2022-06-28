package com.ffanaticism.intruder.telegramhandler.client.impl;

import com.ffanaticism.intruder.telegramhandler.client.TelegramWebClient;
import com.ffanaticism.intruder.telegramhandler.config.TelegramHandlerProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Slf4j
@Component
public class DefaultTelegramWebClient implements TelegramWebClient {
    private final RestTemplate restTemplate;
    private final TelegramHandlerProperty properties;

    @Autowired
    public DefaultTelegramWebClient(RestTemplate restTemplate, TelegramHandlerProperty properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public void setWebhookPath(String url) {
        var finalUrl = properties.getTelegramApiBotUrl()
                .concat(properties.getBotToken())
                .concat("/setWebhook?url=")
                .concat(url);
        var response = restTemplate.exchange(finalUrl, HttpMethod.GET,
                getHttpEntityHeader(), Void.class);
        log.info("Set webhook path: '{}', status: {}", url, response.getStatusCode());
    }

    private HttpEntity<String> getHttpEntityHeader() {
        var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>("", headers);
    }
}
