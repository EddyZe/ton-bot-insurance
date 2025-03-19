package ru.eddyz.telegrambot.commands;


import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.eddyz.telegrambot.domain.enums.VotingSolution;

public interface VoteCommand extends MessageCommand{

    void vote(CallbackQuery callbackQuery, VotingSolution vote);

}
