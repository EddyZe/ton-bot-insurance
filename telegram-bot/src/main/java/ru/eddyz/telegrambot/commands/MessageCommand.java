package ru.eddyz.telegrambot.commands;


import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface MessageCommand {

    void execute(Message message);
}
