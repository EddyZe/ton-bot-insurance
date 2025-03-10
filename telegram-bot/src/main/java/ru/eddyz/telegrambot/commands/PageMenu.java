package ru.eddyz.telegrambot.commands;



public interface PageMenu {
    void nextPage(Long chatId);
    void prevPage(Long chatId);
}
