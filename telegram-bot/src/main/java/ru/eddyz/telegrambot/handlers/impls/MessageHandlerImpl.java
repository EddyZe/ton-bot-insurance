package ru.eddyz.telegrambot.handlers.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.eddyz.telegrambot.commands.OpenWalletCommand;
import ru.eddyz.telegrambot.commands.ProfileCommand;
import ru.eddyz.telegrambot.commands.StartCommand;
import ru.eddyz.telegrambot.domain.enums.ButtonsText;
import ru.eddyz.telegrambot.handlers.MessageHandler;


@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandlerImpl implements MessageHandler {

    private final StartCommand startCommand;
    private final ProfileCommand profileCommand;
    private final OpenWalletCommand openWalletCommand;

    @Override
    public void handle(Message message) {
        if (message.hasText()) {
            textCommandHandle(message);
        }
    }

    private void textCommandHandle(Message message) {
        var text = message.getText();

        if (text.equals("/start")) {
            startCommand.execute(message);
            return;
        }

        if (text.equals(ButtonsText.PROFILE.toString())) {
            profileCommand.execute(message);
            return;
        }

        if (text.equals(ButtonsText.WALLET.toString())) {
            openWalletCommand.execute(message);
            return;
        }

        if (text.equals(ButtonsText.PAYMENTS.toString())) {
            //TODO Реализовать команду историю платежей
        }

        if (text.equals(ButtonsText.INSURANCE.toString())) {
            //TODO Реализовать команду просмотра страховки
        }
    }
}
