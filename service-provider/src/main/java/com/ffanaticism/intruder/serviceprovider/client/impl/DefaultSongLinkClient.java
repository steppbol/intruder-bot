package com.ffanaticism.intruder.serviceprovider.client.impl;

import com.ffanaticism.intruder.serviceprovider.client.SongLinkClient;
import com.ffanaticism.intruder.serviceprovider.config.ServiceProviderProperty;
import com.ffanaticism.intruder.serviceprovider.entity.PlatformEntity;
import com.ffanaticism.intruder.serviceprovider.exception.NotSupportedPlatformException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
public class DefaultSongLinkClient implements SongLinkClient {
    private final ServiceProviderProperty properties;
    private final RestTemplate restTemplate;

    @Autowired
    public DefaultSongLinkClient(ServiceProviderProperty properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    @Override
    public PlatformEntity getPlatform(String source) throws NotSupportedPlatformException {
        PlatformEntity platformEntity = null;

        try {
            var finalUrl = properties.getSongLinkUrl() +
                    "/links?url=" +
                    URLEncoder.encode(source, StandardCharsets.UTF_8);
            var response = restTemplate.exchange(finalUrl, HttpMethod.GET,
                    getHttpEntityHeader(), PlatformEntity.class);

            if (response.getStatusCode().value() == HttpStatus.SC_OK) {
                var body = response.getBody();
                if (body != null) {
                    platformEntity = body;
                }
            } else if (response.getStatusCode().value() != HttpStatus.SC_BAD_REQUEST) {
                throw new NotSupportedPlatformException();
            }
        } catch (HttpClientErrorException e) {
            throw new NotSupportedPlatformException();
        }

        return platformEntity;
    }

    private HttpEntity<String> getHttpEntityHeader() {
        var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>("", headers);
    }
}
