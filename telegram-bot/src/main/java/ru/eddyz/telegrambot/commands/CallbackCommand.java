package ru.eddyz.telegrambot.commands;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackCommand {

    void execute(CallbackQuery callbackQuery);
}
