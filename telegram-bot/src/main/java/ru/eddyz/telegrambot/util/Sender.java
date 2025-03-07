package ru.eddyz.telegrambot.util;


import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;


public class Sender {

    public static SendMessage sendMessage(Long chatId, String text) {
        return SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .parseMode(ParseMode.HTML)
                .build();
    }

    public static SendMessage sendMessage(Long chatId, String text, ReplyKeyboard keyboard) {
        return SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .replyMarkup(keyboard)
                .parseMode(ParseMode.HTML)
                .build();
    }
}
