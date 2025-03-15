package ru.eddyz.adminpanel.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import util.Constant;

@Configuration
public class TelegramConfig {

    @Bean
    public TelegramClient telegramClient(@Value("${telegram.bot_token}") String botToken) {
        return new OkHttpTelegramClient(botToken);
    }

    @Bean
    public RestClient restClient(@Value("${telegram.bot_token}") String botToken,
                                 @Value("${telegram.bot_url}") String botUrl) {
        return RestClient.builder()
                .baseUrl(botUrl)
                .defaultHeaders(headers -> headers.add(Constant.BOT_TOKEN, botToken))
                .build();
    }
}
