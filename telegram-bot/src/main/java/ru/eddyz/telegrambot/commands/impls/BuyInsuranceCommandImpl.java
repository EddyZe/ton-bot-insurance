package ru.eddyz.telegrambot.commands.impls;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.BuyInsuranceCommand;
import ru.eddyz.telegrambot.commands.ShowInsuranceCommand;
import ru.eddyz.telegrambot.domain.entities.Insurance;
import ru.eddyz.telegrambot.repositories.InsuranceRepository;
import ru.eddyz.telegrambot.repositories.UserRepository;
import ru.eddyz.telegrambot.repositories.WalletRepository;
import ru.eddyz.telegrambot.util.Sender;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Slf4j
@Component
@RequiredArgsConstructor
public class BuyInsuranceCommandImpl implements BuyInsuranceCommand {

    private final TelegramClient telegramClient;
    private final InsuranceRepository insuranceRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final ShowInsuranceCommand showInsuranceCommand;


    @Value("${insurance.price}")
    private Double insurancePrice;

    @Value("${insurance.period}")
    private Integer insuranceDays;


    @Override
    @Transactional
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        answerCallback(callbackQuery.getId());

        var userOp = userRepository.findByTelegramChatId(chatId);

        if (userOp.isEmpty()) {
            sendMessage(chatId, "Ваш аккаунт не активирован. Чтобы активировать аккаунт введите команду /start");
            return;
        }

        var user = userOp.get();

        if (user.getWallet() == null) {
            sendMessage(chatId, "TON Кошелек не привязан! Чтобы привязать кошелек, откройте кошелек и отправьте номер счета. Затем пополните баланс!");
            return;
        }

        var wallet = user.getWallet();

        if (wallet.getBalance() < insurancePrice) {
            sendMessage(chatId, "Недостаточно средств! Пополните баланс! Для этого откройте кошелек и нажмите пополнить баланс!");
            return;
        }

        if (insuranceRepository.findByChatIdIsActive(chatId).isPresent()) {
            sendMessage(chatId, "Страховка еще активна!");
            return;
        }

        var currentDate = LocalDateTime.now();
        var endDate = currentDate.plusDays(insuranceDays);
        var newInsurance = Insurance.builder()
                .active(true)
                .amount(insurancePrice)
                .currency(wallet.getToken())
                .startDate(currentDate)
                .endDate(endDate)
                .user(user)
                .build();

        var dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        insuranceRepository.save(newInsurance);
        wallet.setBalance(wallet.getBalance() - insurancePrice);
        walletRepository.save(wallet);
        sendMessage(chatId, "Поздравляем с успешной покупкой! Дата окончания страховки: %s🎉".formatted(endDate.format(dtf)));

        try {
            var deleteMessage = DeleteMessage.builder()
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .chatId(chatId)
                    .build();
            telegramClient.execute(deleteMessage);
            showInsuranceCommand.execute((Message) callbackQuery.getMessage());
        } catch (TelegramApiException e) {
            log.error("error deleting message BuyInsuranseCommand", e);
        }
    }

    private void answerCallback(String id) {
        try {
            telegramClient.execute(new AnswerCallbackQuery(id));
        } catch (TelegramApiException e) {
            log.error("error answering callback", e);
        }
    }

    private void sendMessage(long chatId, String message) {
        try {
            telegramClient.execute(Sender.sendMessage(chatId, message));
        } catch (TelegramApiException e) {
            log.error("errror send message BuyInsuranceCommand", e);
        }
    }
}
