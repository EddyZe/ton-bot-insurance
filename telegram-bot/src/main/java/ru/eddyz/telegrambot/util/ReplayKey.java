package ru.eddyz.telegrambot.util;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.eddyz.telegrambot.domain.enums.ButtonsText;

import java.util.List;

@Component
public class ReplayKey {


    public ReplyKeyboardMarkup mainMenu() {
        return ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .keyboard(
                        List.of(
                                new KeyboardRow(ButtonsText.ADD_HISTORY.toString()),
                                new KeyboardRow(ButtonsText.PROFILE.toString(), ButtonsText.WALLET.toString()),
                                new KeyboardRow(ButtonsText.INSURANCE.toString(), ButtonsText.PAYMENTS.toString())
                        )
                )
                .build();
    }

}
