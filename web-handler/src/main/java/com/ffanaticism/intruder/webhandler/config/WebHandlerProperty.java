package com.ffanaticism.intruder.webhandler.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource(value = "classpath:web-handler.yml", factory = YamlPropertySourceFactory.class)
public class WebHandlerProperty {
}
