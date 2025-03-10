package ru.eddyz.telegrambot.commands.impls;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.WithdrawCommand;
import ru.eddyz.telegrambot.domain.entities.Withdraw;
import ru.eddyz.telegrambot.domain.enums.ButtonsIds;
import ru.eddyz.telegrambot.domain.enums.WithdrawStatus;
import ru.eddyz.telegrambot.repositories.UserRepository;
import ru.eddyz.telegrambot.repositories.WalletRepository;
import ru.eddyz.telegrambot.repositories.WithdrawRepository;
import ru.eddyz.telegrambot.util.DataStore;
import ru.eddyz.telegrambot.util.Sender;

import java.time.LocalDateTime;


@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawCommandImpl implements WithdrawCommand {

    private final TelegramClient telegramClient;

    private final UserRepository userRepository;
    private final WithdrawRepository withdrawRepository;
    private final WalletRepository walletRepository;

    @Value("${insurance.token.name}")
    private String tokenName;
    @Value("${telegram.admin_username}")
    private String adminUsername;


    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        DataStore.currentCommand.put(chatId, ButtonsIds.WITHDRAW_MONEY);

        editMessage(chatId, callbackQuery.getMessage().getMessageId(), "Введите сумму, которую хотите снять:");

    }

    @Override
    @Transactional
    public void execute(Message message) {
        var chatId = message.getChatId();
        double amount;

        try {
            amount = Double.parseDouble(message.getText());
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Вводите только цифры! Повторите попытку!");
            return;
        }

        var userOp = userRepository.findByTelegramChatId(chatId);

        if (userOp.isEmpty()) {
            sendMessage(chatId, "Ваш аккаунт не активирован, чтобы активировать аккаунт введите команду /start");
            return;
        }

        var walletOp = walletRepository.findByUserTelegramId(chatId);

        if (walletOp.isEmpty()) {
            sendMessage(chatId, "Кошелек не активен. Привяжите номер кошелька!");
            return;
        }

        if (walletOp.get().getBalance() < amount) {
            sendMessage(chatId, "Сумма вывода, не должна быть больше, чем текущий баланс!");
            return;
        }

        var withdraw = Withdraw.builder()
                .amount(amount)
                .token(tokenName)
                .active(true)
                .status(WithdrawStatus.AWAITING)
                .wallet(walletOp.get())
                .createdAt(LocalDateTime.now())
                .user(userOp.get())
                .build();

        withdrawRepository.save(withdraw);

        sendMessage(chatId, "Заявка на снятие средств отправлена, вы получите уведомление, как мы обработаем вашу заявку");

        userRepository.findByUsername(adminUsername)
                .ifPresent(user ->
                        sendMessage(user.getTelegramChatId(),
                                "❗ Новая заявка на снятие средств от пользователя <b>%s</b> на сумму <b>%.2f %s</b>. Не забудьте обработать заявку в панели администратора!"
                                        .formatted(userOp.get().getUsername(), withdraw.getAmount(), tokenName)));

    }

    private void sendMessage(Long chatId, String message) {
        try {
            var sendMessage = Sender.sendMessage(chatId, message);
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("error sendMessage to withdrawCommand", e);
        }
    }

    private void editMessage(Long chatId, Integer messageId, String message) {
        try {
            var sendMessage = EditMessageText.builder()
                    .text(message)
                    .messageId(messageId)
                    .chatId(chatId)
                    .build();

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error edit to WithdrawCommand", e);
        }
    }
}
