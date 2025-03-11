package ru.eddyz.telegrambot.handlers.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.*;
import ru.eddyz.telegrambot.domain.enums.ButtonsIds;
import ru.eddyz.telegrambot.handlers.CallBackHandler;
import ru.eddyz.telegrambot.util.DataStore;
import ru.eddyz.telegrambot.util.Sender;


@Slf4j
@Component
@RequiredArgsConstructor
public class CallBackHandlerImpl implements CallBackHandler {

    private final TelegramClient telegramClient;

    private final InstallWalletCommand installWalletCommand;
    private final UpBalanceCommand upBalanceCommand;
    private final WithdrawCommand withdrawCommand;
    private final HistoryWithdrawCommand historyWithdrawCommand;
    private final HistoryPayments historyPayments;
    private final BuyInsuranceCommand buyInsuranceCommand;
    private final HistoryInsuranceCommand historyInsuranceCommand;
    private final OpenHistoryListCommand openHistoryListCommand;
    private final OpenAllFilesHistory openAllFilesHistory;
    private final DeleteHistoryCommand deleteHistoryCommand;
    private final SetPaymentAmountCommand setPaymentAmountCommand;


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
            DataStore.currentPageHistoryWithdraw.remove(chatId);
            deleteMessage(chatId, messageId);
            return;
        }

        if (data.equals(ButtonsIds.PAYMENT_NEXT_BUTTON.name())) {
            historyPayments.nextPage(chatId);
            historyPayments.execute(callbackQuery);
            return;
        }

        if (data.equals(ButtonsIds.PAYMENT_PREV_BUTTON.name())) {
            historyPayments.prevPage(chatId);
            historyPayments.execute(callbackQuery);
            return;
        }

        if (data.equals(ButtonsIds.PAYMENT_CLOSE.name())) {
            DataStore.currentPageHistoryPayments.remove(chatId);
            deleteMessage(chatId, messageId);
            return;
        }

        if (data.equals(ButtonsIds.INSURANCE_BUY.name())) {
            buyInsuranceCommand.execute(callbackQuery);
            return;
        }

        if (data.equals(ButtonsIds.INSURANCE_HISTORY.name())) {
            historyInsuranceCommand.execute(callbackQuery);
            return;
        }

        if (data.equals(ButtonsIds.INSURANCE_NEXT_BUTTON.name())) {
            historyInsuranceCommand.nextPage(chatId);
            historyInsuranceCommand.execute(callbackQuery);
            return;
        }

        if (data.equals(ButtonsIds.INSURANCE_PREV_BUTTON.name())) {
            historyInsuranceCommand.prevPage(chatId);
            historyInsuranceCommand.execute(callbackQuery);
            return;
        }

        if (data.equals(ButtonsIds.INSURANCE_CLOSE.name())) {
            DataStore.currentPageHistoryInsurance.remove(chatId);
            deleteMessage(chatId, messageId);
            return;
        }

        if (data.equals(ButtonsIds.HISTORY_NEXT_BUTTON.name())) {
            deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
            openHistoryListCommand.nextPage(chatId);
            openHistoryListCommand.execute(callbackQuery);
            return;
        }

        if (data.equals(ButtonsIds.HISTORY_PREV_BUTTON.name())) {
            deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
            openHistoryListCommand.prevPage(chatId);
            openHistoryListCommand.execute(callbackQuery);
            return;
        }

        if (data.equals(ButtonsIds.HISTORY_CLOSE.name())) {
            DataStore.currentPageHistoryInsurance.remove(chatId);
            deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
            return;
        }

        if (data.startsWith(ButtonsIds.HISTORY_ALL_FILES_BUTTON.name())) {
            openAllFilesHistory.execute(callbackQuery);
            return;
        }

        if (data.startsWith(ButtonsIds.HISTORY_REMOVE_BUTTON.name())) {
            deleteHistoryCommand.execute(callbackQuery);
            return;
        }

        if (data.startsWith(ButtonsIds.HISTORY_PRICE_BUTTON.name())) {
            setPaymentAmountCommand.execute(callbackQuery);
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
