package com.ffanaticism.intruder.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static com.ffanaticism.intruder.telegramhandler.controller.ApiPath.API_PATH;
import static com.ffanaticism.intruder.telegramhandler.controller.ApiPath.EVENTS_PATH;
import static com.ffanaticism.intruder.telegramhandler.controller.ApiPath.TELEGRAM_HANDLER_PATH;
import static com.ffanaticism.intruder.telegramhandler.controller.ApiPath.V1_PATH;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = {"com.ffanaticism.intruder"})
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable().authorizeRequests()
                .antMatchers(API_PATH + V1_PATH + TELEGRAM_HANDLER_PATH + EVENTS_PATH + "/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin();
    }
}