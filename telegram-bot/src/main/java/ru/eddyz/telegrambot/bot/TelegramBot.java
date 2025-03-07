package ru.eddyz.telegrambot.bot;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.sync.Tonapi;

import java.util.List;


@Getter
@Component
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String botToken;
    private final String botUsername;
    private final Tonapi tonapi;

    @Value("${ton-api.account_id}")
    private String accId;

    public TelegramBot(@Value("${telegram.bot_token}") String botToken,
                       @Value("${telegram.bot_username}") String botUsername, Tonapi tonapi) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.tonapi = tonapi;
    }


    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var acc = tonapi.getAccounts().getJettonsBalances(accId, null, null);
            acc.getBalances().forEach(accBalance -> {
                System.out.println(accBalance.getJetton().getName());
                System.out.println(accBalance.getJetton().getSymbol());
                System.out.println(accBalance.getBalance());
            });
        }
    }
}
