package ru.eddyz.telegrambot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.StartCommand;
import ru.eddyz.telegrambot.domain.entities.User;
import ru.eddyz.telegrambot.repositories.UserRepository;
import ru.eddyz.telegrambot.util.ReplayKey;
import ru.eddyz.telegrambot.util.Sender;

import java.time.LocalDateTime;


@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommandImpl implements StartCommand {

    private final TelegramClient telegramClient;
    private final ReplayKey replayKey;

    private final UserRepository userRepository;


    @Override
    public void execute(Message message) {
        var chatId = message.getChatId();
        var username = message.getChat().getUserName() == null ? "unknown" : message.getChat().getUserName();

        saveUser(username, chatId);

        try{
            var sendMessage = Sender.sendMessage(chatId, generateMessage(), replayKey.mainMenu());
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error sendMessage startCommand", e);
        }

    }

    private void saveUser(String username, Long chatId) {
        var userOptional = userRepository.findByTelegramChatId(chatId);

        if  (userOptional.isEmpty()) {
            var user = buildUser(username, chatId);
            userRepository.save(user);
            return;
        }

        var user = userOptional.get();

        if (user.getUsername().equals("unknown") && !username.equals("unknown")) {
            user.setUsername(username);
            userRepository.save(user);
        }
    }

    private User buildUser(String username,  Long chatId) {
        return User.builder()
                .username(username)
                .telegramChatId(chatId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();
    }

    private String generateMessage() {
        return "Приветствие";
    }
}
