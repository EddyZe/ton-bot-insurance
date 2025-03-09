package ru.eddyz.telegrambot.util;


import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.eddyz.telegrambot.domain.enums.ButtonsIds;
import ru.eddyz.telegrambot.domain.enums.ButtonsText;

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
        var closeWallet = InlineKeyboardButton.builder()
                .callbackData(ButtonsIds.CLOSE_WALLET.name())
                .text(ButtonsText.CLOSE.toString())
                .build();
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        new InlineKeyboardRow(installNumberWallet),
                        new InlineKeyboardRow(upBalance),
                        new InlineKeyboardRow(withdrawMoney),
                        new InlineKeyboardRow(closeWallet)))
                .build();
    }

}
