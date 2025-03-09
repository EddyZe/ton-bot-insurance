package ru.eddyz.telegrambot.commands.impls;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.clients.tonapi.ton.exception.TONAPIBadRequestError;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.sync.Tonapi;
import ru.eddyz.telegrambot.commands.OpenWalletCommand;
import ru.eddyz.telegrambot.domain.entities.User;
import ru.eddyz.telegrambot.domain.entities.Wallet;
import ru.eddyz.telegrambot.domain.enums.ButtonsText;
import ru.eddyz.telegrambot.repositories.UserRepository;
import ru.eddyz.telegrambot.repositories.WalletRepository;
import ru.eddyz.telegrambot.util.InlineKey;
import ru.eddyz.telegrambot.util.Sender;

import java.time.LocalDateTime;


@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class OpenWalletCommandImpl implements OpenWalletCommand {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    private final TelegramClient telegramClient;
    private final InlineKey inlineKey;

    private final Tonapi tonapi;

    @Value("${insurance.token.name}")
    private String tokenName;
    @Value("${insurance.token.symbol}")
    private String tokenSymbol;


    @Override
    public void execute(CallbackQuery callbackQuery) {

    }

    @Override
    public void execute(Message message) {
        var chatId = message.getChatId();

        getWallet(chatId);
    }

    private void getWallet(Long chatId) {
        var walletOp = walletRepository.findByUserTelegramId(chatId);

        if (walletOp.isEmpty()) {
            var userOp = userRepository.findByTelegramChatId(chatId);
            if (userOp.isEmpty()) {
                sendMessage(chatId, "Ваш аккаунт не активирован, чтобы активировать аккаунт - введите команду /start.", null);
                return;
            }

            var newWallet = buildWallet(userOp.get());
            newWallet = walletRepository.save(newWallet);

            sendMessage(chatId, generateMessage(newWallet, 0.), inlineKey.walletButtons());
            return;
        }

        var wallet = walletOp.get();
        if (!wallet.getToken().equals(tokenName)) {
            wallet.setToken(tokenName);
            wallet = walletRepository.save(wallet);
        }


        try {
            var res = tonapi.getAccounts().getJettonsBalances(wallet.getAccountId(), null, null);
            var balance = res.getBalances().stream()
                    .filter(j -> j.getJetton().getName().equals(tokenName) || j.getJetton().getSymbol().equals(tokenSymbol))
                    .map(j -> Double.parseDouble(j.getBalance()) / 1_000_000_000)
                    .findFirst()
                    .orElse(0.);
            sendMessage(chatId, generateMessage(wallet, balance), inlineKey.walletButtons());
        } catch (TONAPIBadRequestError e) {
            sendMessage(chatId, generateMessage(wallet, 0.), inlineKey.walletButtons());
        }
    }


    private void sendMessage(Long chatId, String text, ReplyKeyboard keyboard) {
        try {
            var sendMessage = keyboard == null ? Sender.sendMessage(chatId, text) : Sender.sendMessage(chatId, text, keyboard);
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error sendMessage openWalletCommand: {}", e.getMessage());
        }
    }


    private Wallet buildWallet(User user) {
        return Wallet.builder()
                .token(tokenName)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .balance(0.)
                .user(user)
                .active(true)
                .accountId("Номер кошелька не указан")
                .build();
    }

    private String generateMessage(Wallet wallet, Double balance) {
        return """
                <b> %s </b>
                
                <b>Номер кошелька: </b> %s
                <b>Баланс:</b> %.2f %s
                <b>Баланс привязанного кошелька: </b> %.2f %s
                """.formatted(
                ButtonsText.WALLET.toString(),
                wallet.getAccountId(),
                wallet.getBalance(),
                wallet.getToken(),
                balance,
                wallet.getToken()
        );
    }
}
