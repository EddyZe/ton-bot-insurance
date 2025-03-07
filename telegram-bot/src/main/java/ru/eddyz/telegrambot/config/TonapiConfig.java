package ru.eddyz.telegrambot.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.sync.Tonapi;

@Configuration
public class TonapiConfig {


    @Bean
    public Tonapi tonapiClient(@Value("${ton-api.token}") String token) {
        return new Tonapi(token, false, 20);
    }

}
