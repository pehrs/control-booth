package com.pehrs.cb.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration("htmlConfig")
public class HtmlConfig {

    @Value("${cb.html.client.id}")
    String clientId;

    @Value("${cb.html.client.secret}")
    String clientSecret;

}
