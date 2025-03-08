package ru.eddyz.telegrambot.bot;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.eddyz.telegrambot.handlers.CallBackHandler;
import ru.eddyz.telegrambot.handlers.MessageHandler;


@Getter
@Component
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String botToken;
    private final String botUsername;

    private final MessageHandler messageHandler;
    private final CallBackHandler callBackHandler;

    public TelegramBot(@Value("${telegram.bot_token}") String botToken,
                       @Value("${telegram.bot_username}") String botUsername,
                       MessageHandler messageHandler, CallBackHandler callBackHandler) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.messageHandler = messageHandler;
        this.callBackHandler = callBackHandler;
    }


    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().isUserMessage()) {
            messageHandler.handle(update.getMessage());
            return;
        }

        if (update.hasCallbackQuery()) {
            callBackHandler.handle(update.getCallbackQuery());
            return;
        }
    }
}
