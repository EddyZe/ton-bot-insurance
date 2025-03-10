package ru.eddyz.telegrambot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.clients.tonapi.ton.exception.TONAPIBadRequestError;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.sync.Tonapi;
import ru.eddyz.telegrambot.commands.InstallWalletCommand;
import ru.eddyz.telegrambot.commands.OpenWalletCommand;
import ru.eddyz.telegrambot.domain.enums.ButtonsIds;
import ru.eddyz.telegrambot.repositories.WalletRepository;
import ru.eddyz.telegrambot.util.DataStore;
import ru.eddyz.telegrambot.util.Sender;


@Slf4j
@Component
@RequiredArgsConstructor
public class InstallWalletCommandImpl implements InstallWalletCommand {

    private final TelegramClient telegramClient;

    private final WalletRepository walletRepository;

    private final Tonapi tonapi;

    private final OpenWalletCommand openWalletCommand;


    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();

        DataStore.currentCommand.put(chatId, ButtonsIds.INSTALL_NUMBER_WALLET);
        editMessage(chatId, callbackQuery.getMessage().getMessageId());

        answerCallBack(callbackQuery.getId());

    }

    @Override
    public void execute(Message message) {
        var chatId = message.getChatId();
        var numberWallet = message.getText();

        try {
            tonapi.getAccounts().getInfo(numberWallet);
        } catch (TONAPIBadRequestError e) {
            log.error("Invalid ton wallet {}", e.getMessage());
            sendMessage(chatId, "Введите существующий номер кошелька!");
            return;
        }

        walletRepository.findByUserTelegramId(chatId).ifPresent(wallet -> {
            if (walletRepository.findByAccountId(numberWallet).isPresent()) {
                sendMessage(chatId, "Данный кошелек уже привязан другим пользователем!");
                openWalletCommand.execute(message);
                return;
            }
            wallet.setAccountId(numberWallet);
            walletRepository.save(wallet);
            sendMessage(chatId, "Номер счета успешно установлен.");
            openWalletCommand.execute(message);
        });
        DataStore.currentCommand.remove(chatId);
    }

    private void answerCallBack(String id) {
        try {
            telegramClient.execute(new AnswerCallbackQuery(id));
        } catch (TelegramApiException e) {
            log.error("error answer call back {}", e.getMessage());
        }
    }

    private void sendMessage(Long chatId, String message) {
        var sendMessage = Sender.sendMessage(chatId, message);

        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error sendMessage to installWalletCommand", e);
        }
    }

    private void editMessage(Long chatId, Integer messageId) {
        var editMessage = EditMessageText.builder()
                .messageId(messageId)
                .chatId(chatId)
                .text("Отправить номер кошелька, который хотите привязать к аккаунту. Обратите внимание, что баланс кошелька отображает токен, который мы используем.")
                .build();

        try {
            telegramClient.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Error editMessage to InstallWalletCommandImpl", e);
        }
    }
}
