package ru.eddyz.telegrambot.commands.impls;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.ProfileCommand;
import ru.eddyz.telegrambot.domain.entities.User;
import ru.eddyz.telegrambot.repositories.UserRepository;
import ru.eddyz.telegrambot.util.Sender;

import java.time.format.DateTimeFormatter;


@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class ProfileCommandImpl implements ProfileCommand {

    private final TelegramClient telegramClient;

    private final UserRepository userRepository;

    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        getProfile(chatId);

        try {
            telegramClient.execute(new AnswerCallbackQuery(callbackQuery.getId()));
        } catch (TelegramApiException e) {
            log.error("Error while executing profile command", e);
        }
    }

    @Override
    public void execute(Message message) {
        var chatId = message.getChatId();
        getProfile(chatId);
    }

    private void getProfile(Long chatId) {
        var userOp = userRepository.findByTelegramChatId(chatId);

        if (userOp.isEmpty()) {
            sendMessage(
                    chatId,
                    "Аккаунт не активирован, чтобы активировать аккаунт введите команду /start",
                    null
            );
            return;
        }

        sendMessage(
                chatId,
                generateMessage(userOp.get()),
                null
        );
    }

    private String generateMessage(User user) {
        var dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return """
                <b>Профиль %s</b>
                
                <b>Баланс: </b> %.2f %s
                <b>Аккаунт создан: </b>%s
                <b>Историй: </b>%d
                """.formatted(
                user.getUsername(),
                user.getWallet() == null ? 0 : user.getWallet().getBalance(),
                user.getWallet() == null ? "" : user.getWallet().getToken(),
                user.getCreatedAt().format(dtf),
                user.getHistories().size()

        );
    }

    private void sendMessage(Long chatId, String message, ReplyKeyboard keyboard) {
        try {
            var sendMessage = keyboard != null ? Sender.sendMessage(chatId, message, keyboard) : Sender.sendMessage(chatId, message);
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error sendMessage profileCommand: {}", e.toString());
        }
    }
}
