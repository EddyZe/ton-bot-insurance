package ru.eddyz.telegrambot.handlers.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.eddyz.telegrambot.commands.*;
import ru.eddyz.telegrambot.domain.enums.ButtonsIds;
import ru.eddyz.telegrambot.domain.enums.ButtonsText;
import ru.eddyz.telegrambot.handlers.MessageHandler;
import ru.eddyz.telegrambot.util.DataStore;


@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandlerImpl implements MessageHandler {

    private final StartCommand startCommand;
    private final ProfileCommand profileCommand;
    private final OpenWalletCommand openWalletCommand;
    private final InstallWalletCommand installWalletCommand;
    private final WithdrawCommand withdrawCommand;
    private final HistoryPayments historyPayments;
    private final ShowInsuranceCommand showInsuranceCommand;
    private final AddHistoryCommand addHistoryCommand;
    private final OpenHistoryListCommand openHistoryListCommand;
    private final SetPaymentAmountCommand setPaymentAmountCommand;

    @Override
    public void handle(Message message) {
        if (message.hasText()) {
            textCommandHandle(message);
            return;
        }

        if (message.hasPhoto()) {
            photoCommandHandler(message);
            return;
        }

        if (message.hasVideo()) {
            videoCommandHandler(message);
            return;
        }

        if (message.hasDocument()) {
            documentCommandHandler(message);
        }
    }

    private void documentCommandHandler(Message message) {
        if (DataStore.currentCommand.containsKey(message.getChatId())) {
            var currentCommand = DataStore.currentCommand.get(message.getChatId());
            if (currentCommand.equals(ButtonsText.ADD_HISTORY.name())) {
                addHistoryCommand.execute(message);
            }
        }
    }

    private void videoCommandHandler(Message message) {
        if (DataStore.currentCommand.containsKey(message.getChatId())) {
            var currentCommand = DataStore.currentCommand.get(message.getChatId());
            if (currentCommand.equals(ButtonsText.ADD_HISTORY.name())) {
                addHistoryCommand.execute(message);
            }
        }
    }

    private void photoCommandHandler(Message message) {
        if (DataStore.currentCommand.containsKey(message.getChatId())) {
            var currentCommand = DataStore.currentCommand.get(message.getChatId());
            if (currentCommand.equals(ButtonsText.ADD_HISTORY.name())) {
                addHistoryCommand.execute(message);
            }
        }
    }

    private void textCommandHandle(Message message) {
        var text = message.getText();


        if (message.getChat().getType().equals("private")) {
            if (text.equals("/start")) {
                startCommand.execute(message);
                return;
            }

            if (text.equals(ButtonsText.PROFILE.toString())) {
                DataStore.currentCommand.remove(message.getChatId());
                profileCommand.execute(message);
                return;
            }

            if (text.equals(ButtonsText.WALLET.toString())) {
                DataStore.currentCommand.remove(message.getChatId());
                openWalletCommand.execute(message);
                return;
            }

            if (text.equals(ButtonsText.PAYMENTS.toString())) {
                DataStore.currentCommand.remove(message.getChatId());
                historyPayments.execute(message);
                return;
            }

            if (text.equals(ButtonsText.INSURANCE.toString())) {
                DataStore.currentCommand.remove(message.getChatId());
                showInsuranceCommand.execute(message);
                return;
            }

            if (text.equals(ButtonsText.ADD_HISTORY.toString())) {
                DataStore.currentCommand.remove(message.getChatId());
                addHistoryCommand.execute(message);
                return;
            }

            if (text.equals(ButtonsText.MY_HISTORY.toString())) {
                DataStore.currentCommand.remove(message.getChatId());
                openHistoryListCommand.execute(message);
                return;
            }

            if (DataStore.currentCommand.containsKey(message.getChatId())) {
                var currentCommand = DataStore.currentCommand.get(message.getChatId());

                if (currentCommand.equals(ButtonsIds.INSTALL_NUMBER_WALLET.name())) {
                    installWalletCommand.execute(message);
                    return;
                }

                if (currentCommand.equals(ButtonsIds.WITHDRAW_MONEY.name())) {
                    withdrawCommand.execute(message);
                    return;
                }

                if (currentCommand.equals(ButtonsText.ADD_HISTORY.name())) {
                    addHistoryCommand.execute(message);
                    return;
                }

                if (currentCommand.equals(ButtonsIds.HISTORY_PRICE_BUTTON.name())) {
                    setPaymentAmountCommand.execute(message);
                }
            }
        }
    }
}
