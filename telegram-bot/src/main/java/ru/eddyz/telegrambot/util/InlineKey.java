package ru.eddyz.telegrambot.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.eddyz.telegrambot.domain.enums.ButtonsIds;
import ru.eddyz.telegrambot.domain.enums.ButtonsText;
import ru.eddyz.telegrambot.domain.enums.VotingSolution;

import java.util.ArrayList;
import java.util.List;

@Component
public class InlineKey {


    @Value("${insurance.price}")
    private String insurancePrice;

    @Value("${insurance.token.name}")
    private String insuranceCurrency;


    public InlineKeyboardMarkup walletButtons() {
        var installNumberWallet = InlineKeyboardButton.builder()
                .callbackData(ButtonsIds.INSTALL_NUMBER_WALLET.name())
                .text(ButtonsText.INSTALL_WALLET.toString())
                .build();
        var upBalance = InlineKeyboardButton.builder()
                .callbackData(ButtonsIds.UP_BALANCE.name())
                .text(ButtonsText.UP_BALANCE.toString())
                .build();
        var withdrawMoney = InlineKeyboardButton.builder()
                .callbackData(ButtonsIds.WITHDRAW_MONEY.name())
                .text(ButtonsText.WITHDRAW_MONEY.toString())
                .build();
        var withdrawHistory = InlineKeyboardButton.builder()
                .callbackData(ButtonsIds.WITHDRAW_MONEY_HISTORY.name())
                .text(ButtonsText.WITHDRAW_MONEY_HISTORY.toString())
                .build();
        var closeWallet = InlineKeyboardButton.builder()
                .callbackData(ButtonsIds.CLOSE_WALLET.name())
                .text(ButtonsText.CLOSE.toString())
                .build();
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        new InlineKeyboardRow(installNumberWallet),
                        new InlineKeyboardRow(upBalance),
                        new InlineKeyboardRow(withdrawMoney, withdrawHistory),
                        new InlineKeyboardRow(closeWallet)))
                .build();
    }

    public InlineKeyboardMarkup withdrawHistory(int totalPages, int currentPage) {
        var next = InlineKeyboardButton.builder()
                .text(ButtonsText.NEXT_BUTTON.toString())
                .callbackData(ButtonsIds.WITHDRAW_NEXT_BUTTON.name())
                .build();

        var previous = InlineKeyboardButton.builder()
                .text(ButtonsText.PREV_BUTTON.toString())
                .callbackData(ButtonsIds.WITHDRAW_PREV_BUTTON.name())
                .build();

        var close = InlineKeyboardButton.builder()
                .text(ButtonsText.CLOSE.toString())
                .callbackData(ButtonsIds.WITHDRAW_CLOSE.name())
                .build();

        return generatePageMenuButton(totalPages, currentPage, next, previous, close);
    }

    public InlineKeyboardMarkup paymentHistory(int totalPages, int currentPage) {
        var next = InlineKeyboardButton.builder()
                .text(ButtonsText.NEXT_BUTTON.toString())
                .callbackData(ButtonsIds.PAYMENT_NEXT_BUTTON.name())
                .build();

        var previous = InlineKeyboardButton.builder()
                .text(ButtonsText.PREV_BUTTON.toString())
                .callbackData(ButtonsIds.PAYMENT_PREV_BUTTON.name())
                .build();

        var close = InlineKeyboardButton.builder()
                .text(ButtonsText.CLOSE.toString())
                .callbackData(ButtonsIds.PAYMENT_CLOSE.name())
                .build();

        return generatePageMenuButton(totalPages, currentPage, next, previous, close);
    }

    private InlineKeyboardMarkup generatePageMenuButton(int totalPages, int currentPage, InlineKeyboardButton next, InlineKeyboardButton previous, InlineKeyboardButton close) {
        var rows = new ArrayList<InlineKeyboardRow>();
        var nextAndPrev = new ArrayList<InlineKeyboardButton>();

        if (currentPage > 0 && currentPage < totalPages)
            nextAndPrev.add(previous);

        if (currentPage < totalPages - 1)
            nextAndPrev.add(next);

        if (!nextAndPrev.isEmpty())
            rows.add(new InlineKeyboardRow(nextAndPrev));

        rows.add(new InlineKeyboardRow(close));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    public InlineKeyboardMarkup insuranceMenu() {
        var buy = InlineKeyboardButton.builder()
                .text(ButtonsText.BUY_INSURANCE.toString().formatted(insurancePrice, insuranceCurrency))
                .callbackData(ButtonsIds.INSURANCE_BUY.name())
                .build();

        var history = InlineKeyboardButton.builder()
                .text(ButtonsText.HISTORY_INSURANCE.toString())
                .callbackData(ButtonsIds.INSURANCE_HISTORY.name())
                .build();

        var close = InlineKeyboardButton.builder()
                .text(ButtonsText.CLOSE.toString())
                .callbackData(ButtonsIds.INSURANCE_CLOSE.name())
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        new InlineKeyboardRow(buy),
                        new InlineKeyboardRow(history),
                        new InlineKeyboardRow(close)
                ))
                .build();
    }

    public InlineKeyboardMarkup insuranceHistory(int totalPages, int currentPage) {
        var next = InlineKeyboardButton.builder()
                .text(ButtonsText.NEXT_BUTTON.toString())
                .callbackData(ButtonsIds.INSURANCE_NEXT_BUTTON.name())
                .build();
        var previous = InlineKeyboardButton.builder()
                .text(ButtonsText.PREV_BUTTON.toString())
                .callbackData(ButtonsIds.INSURANCE_PREV_BUTTON.name())
                .build();

        var close = InlineKeyboardButton.builder()
                .text(ButtonsText.CLOSE.toString())
                .callbackData(ButtonsIds.INSURANCE_CLOSE.name())
                .build();

        return generatePageMenuButton(totalPages, currentPage, next, previous, close);
    }

    public InlineKeyboardMarkup historyList(int totalPages, int currentPage, long historyId) {
        var publish = InlineKeyboardButton.builder()
                .text(ButtonsText.PUBLISH.toString())
                .callbackData(ButtonsIds.HISTORY_PUBLISH_BUTTON.name() + ":" + historyId)
                .build();
        var allFiles = InlineKeyboardButton.builder()
                .text(ButtonsText.HISTORY_FILES.toString())
                .callbackData(ButtonsIds.HISTORY_ALL_FILES_BUTTON.name() + ":" + historyId)
                .build();
        var price = InlineKeyboardButton.builder()
                .text(ButtonsText.HISTORY_PRICE.toString())
                .callbackData(ButtonsIds.HISTORY_PRICE_BUTTON.name() + ":" + historyId)
                .build();
        var edit = InlineKeyboardButton.builder()
                .text(ButtonsText.EDIT.toString())
                .callbackData(ButtonsIds.HISTORY_EDIT_BUTTON.name() + ":" + historyId)
                .build();
        var next = InlineKeyboardButton.builder()
                .text(ButtonsText.NEXT_BUTTON.toString())
                .callbackData(ButtonsIds.HISTORY_NEXT_BUTTON.name())
                .build();
        var previous = InlineKeyboardButton.builder()
                .text(ButtonsText.PREV_BUTTON.toString())
                .callbackData(ButtonsIds.HISTORY_PREV_BUTTON.name())
                .build();
        var delete = InlineKeyboardButton.builder()
                .text(ButtonsText.REMOVE.toString())
                .callbackData(ButtonsIds.HISTORY_REMOVE_BUTTON.name() + ":" + historyId)
                .build();
        var close = InlineKeyboardButton.builder()
                .text(ButtonsText.CLOSE.toString())
                .callbackData(ButtonsIds.HISTORY_CLOSE.name())
                .build();

        var nextAndPrevious = generatePageMenuButton(totalPages, currentPage, next, previous, close).getKeyboard();

        var rows = new ArrayList<>(List.of(
                new InlineKeyboardRow(publish),
                new InlineKeyboardRow(price),
                new InlineKeyboardRow(allFiles, edit),
                new InlineKeyboardRow(delete)));

        rows.addAll(nextAndPrevious);

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    public InlineKeyboardMarkup votes(Long historyId) {
        var yes = InlineKeyboardButton.builder()
                .text(VotingSolution.VOTING_YES.toString())
                .callbackData(VotingSolution.VOTING_YES.name() + ":" + historyId)
                .build();

        var no = InlineKeyboardButton.builder()
                .text(VotingSolution.VOTING_NO.toString())
                .callbackData(VotingSolution.VOTING_NO.name() + ":" + historyId)
                .build();

        var yesPrice = InlineKeyboardButton.builder()
                .text(VotingSolution.VOTING_SET_PRICE_YES.toString())
                .callbackData(VotingSolution.VOTING_SET_PRICE_YES.name() + ":" + historyId)
                .build();

        var allFiles = InlineKeyboardButton.builder()
                .text(ButtonsText.HISTORY_FILES.toString())
                .callbackData(ButtonsIds.HISTORY_ALL_FILES_BUTTON.name() + ":" + historyId)
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        new InlineKeyboardRow(allFiles),
                        new InlineKeyboardRow(yes, no),
                        new InlineKeyboardRow(yesPrice)
                ))
                .build();
    }

    public InlineKeyboardMarkup resultVotes(Long historyId) {
        var result = InlineKeyboardButton.builder()
                .text(ButtonsText.RESULT_VOTES.toString())
                .callbackData(ButtonsIds.HISTORY_RESULTS_VOTES_BUTTON.name() + ":" + historyId)
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(new InlineKeyboardRow(result)))
                .build();
    }

}
