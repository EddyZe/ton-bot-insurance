package ru.eddyz.telegrambot.util;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.eddyz.telegrambot.domain.enums.ButtonsIds;
import ru.eddyz.telegrambot.domain.enums.ButtonsText;

import java.util.ArrayList;
import java.util.List;

@Component
public class InlineKey {


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

        var rows = new ArrayList<InlineKeyboardRow>();

        if (currentPage > 0 && currentPage < totalPages)
            rows.add(new InlineKeyboardRow(previous));

        if (currentPage < totalPages - 1)
            rows.add(new InlineKeyboardRow(next));

        rows.add(new InlineKeyboardRow(close));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

}
