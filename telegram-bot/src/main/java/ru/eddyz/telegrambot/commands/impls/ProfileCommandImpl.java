package ru.eddyz.telegrambot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.ProfileCommand;
import ru.eddyz.telegrambot.domain.entities.User;
import ru.eddyz.telegrambot.domain.enums.ButtonsText;
import ru.eddyz.telegrambot.repositories.UserRepository;
import ru.eddyz.telegrambot.util.Sender;

import java.time.format.DateTimeFormatter;


@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileCommandImpl implements ProfileCommand {

    private final TelegramClient telegramClient;

    private final UserRepository userRepository;

    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        getProfile(chatId);
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
                <b>%s</b>
                
                <b>%s</b>
                <b>Аккаунт создан: %s</b>
                
                """.formatted(
                ButtonsText.PROFILE.toString(),
                user.getUsername(),
                user.getCreatedAt().format(dtf)
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
