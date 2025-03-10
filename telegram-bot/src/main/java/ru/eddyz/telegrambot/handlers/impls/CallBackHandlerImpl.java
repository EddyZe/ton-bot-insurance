package ru.eddyz.telegrambot.handlers.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.HistoryWithdrawCommand;
import ru.eddyz.telegrambot.commands.InstallWalletCommand;
import ru.eddyz.telegrambot.commands.UpBalanceCommand;
import ru.eddyz.telegrambot.commands.WithdrawCommand;
import ru.eddyz.telegrambot.domain.enums.ButtonsIds;
import ru.eddyz.telegrambot.handlers.CallBackHandler;
import ru.eddyz.telegrambot.util.Sender;


@Slf4j
@Component
@RequiredArgsConstructor
public class CallBackHandlerImpl implements CallBackHandler {

    private final TelegramClient telegramClient;

    private final InstallWalletCommand installWalletCommand;
    private final UpBalanceCommand upBalanceCommand;
    private final WithdrawCommand withdrawCommand;
    private final HistoryWithdrawCommand  historyWithdrawCommand;


    @Override
    public void handle(CallbackQuery callbackQuery) {
        var data = callbackQuery.getData();
        var messageId = callbackQuery.getMessage().getMessageId();
        var chatId = callbackQuery.getMessage().getChatId();

        if (data.equals(ButtonsIds.CLOSE_WALLET.name())) {
            deleteMessage(chatId, messageId);
            return;
        }

        if (data.equals(ButtonsIds.INSTALL_NUMBER_WALLET.name())) {
            installWalletCommand.execute(callbackQuery);
            return;
        }

        if (data.equals(ButtonsIds.UP_BALANCE.name())) {
            upBalanceCommand.execute(callbackQuery);
            return;
        }

        if (data.equals(ButtonsIds.WITHDRAW_MONEY.name())) {
            withdrawCommand.execute(callbackQuery);
            return;
        }

        if (data.equals(ButtonsIds.WITHDRAW_MONEY_HISTORY.name())) {
            historyWithdrawCommand.execute(callbackQuery);
            return;
        }

        if (data.equals(ButtonsIds.WITHDRAW_NEXT_BUTTON.name())) {
            historyWithdrawCommand.nextPage(callbackQuery.getFrom().getId());
            historyWithdrawCommand.execute(callbackQuery);
            return;
        }

        if (data.equals(ButtonsIds.WITHDRAW_PREV_BUTTON.name())) {
            historyWithdrawCommand.prevPage(callbackQuery.getFrom().getId());
            historyWithdrawCommand.execute(callbackQuery);
            return;
        }

        if (data.equals(ButtonsIds.WITHDRAW_CLOSE.name())) {
            deleteMessage(chatId, messageId);
            return;
        }
    }


    private void deleteMessage(Long chatId, Integer messageId) {
        try {
            telegramClient.execute(Sender.deleteMessage(chatId, messageId));
        } catch (TelegramApiException e) {
            log.error("Delete message error", e);
        }
    }
}
