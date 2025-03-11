package ru.eddyz.telegrambot.commands.impls;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.clients.tonapi.ton.exception.TONAPIBadRequestError;
import ru.eddyz.telegrambot.commands.AddHistoryCommand;
import ru.eddyz.telegrambot.commands.OpenWalletCommand;
import ru.eddyz.telegrambot.domain.entities.History;
import ru.eddyz.telegrambot.domain.entities.HistoryFile;
import ru.eddyz.telegrambot.domain.entities.User;
import ru.eddyz.telegrambot.domain.enums.AddHistoryState;
import ru.eddyz.telegrambot.domain.enums.ButtonsText;
import ru.eddyz.telegrambot.domain.enums.HistoryFileType;
import ru.eddyz.telegrambot.domain.enums.HistoryStatus;
import ru.eddyz.telegrambot.repositories.HistoryFilesRepository;
import ru.eddyz.telegrambot.repositories.HistoryRepository;
import ru.eddyz.telegrambot.repositories.InsuranceRepository;
import ru.eddyz.telegrambot.repositories.UserRepository;
import ru.eddyz.telegrambot.util.DataStore;
import ru.eddyz.telegrambot.util.Sender;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Component
@RequiredArgsConstructor
public class AddHistoryCommandImpl implements AddHistoryCommand {

    private final TelegramClient telegramClient;
    private final HistoryRepository historyRepository;
    private final HistoryFilesRepository historyFilesRepository;
    private final InsuranceRepository insuranceRepository;
    private final UserRepository userRepository;

    private final OpenWalletCommand openWalletCommand;

    @Value("${insurance.minimal_tokens}")
    private Double minTokenValue;

    @Value("${insurance.token.name}")
    private String currency;

    private final Map<Long, AddHistoryState> currentState = new HashMap<>();


    @Override
    @Transactional
    public void execute(Message message) {
        var chatId = message.getChatId();

        var userOp = userRepository.findByTelegramChatId(chatId);

        if (userOp.isEmpty()) {
            sendMessage(chatId, "Ваш аккаунт не активирован, чтобы активировать аккаунт - введите команду /start");
            return;
        }

        var user = userOp.get();

        var insurance = insuranceRepository.findByChatIdIsActive(chatId);
        if (insurance.isEmpty()) {
            sendMessage(chatId, "Чтобы добавить историю, купите страховку!");
            return;
        }

        if (checkValueToken(user, chatId)) return;


        if (!DataStore.currentCommand.containsKey(chatId) || !currentState.containsKey(chatId)) {
            sendMessage(chatId, "Отправьте свою историю, так же вы можете прикрепить несколько фото, видео или документ. \n\n❗После отправки истории, добавить файлы будет нельзя.");
            DataStore.currentCommand.put(chatId, ButtonsText.ADD_HISTORY.name());
            currentState.put(chatId, AddHistoryState.HISTORY);
            return;
        }

        var state = currentState.get(chatId);

        if (Objects.requireNonNull(state) == AddHistoryState.HISTORY) {
            if (message.hasPhoto()) {
                saveNewHistory(message.getMediaGroupId(), message.getPhoto().getLast().getFileId(), message.getCaption(), user, chatId, HistoryFileType.PHOTO);
            }

            if (message.hasDocument()) {
                saveNewHistory(message.getMediaGroupId(), message.getDocument().getFileId(), message.getCaption(), user, chatId, HistoryFileType.DOCUMENT);
            }

            if (message.hasVideo()) {
                saveNewHistory(message.getMediaGroupId(), message.getVideo().getFileId(), message.getCaption(), user, chatId, HistoryFileType.VIDEO);
            }

            if (message.hasText()) {
                saveNewHistory("", "", message.getText(), user, chatId, HistoryFileType.TEXT);
            }
        }

        Thread.startVirtualThread(() -> {
            try {
                Thread.sleep(1000 * 60 * 10);
                currentState.remove(chatId);
                DataStore.currentCommand.remove(chatId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void saveNewHistory(String mediaGroupId, String fileId, String caption, User user, Long chatId, HistoryFileType historyFileType) {
        var telegramFileGroupId = mediaGroupId == null ? "" : mediaGroupId;
        if (!telegramFileGroupId.isEmpty()) {
            var files = historyFilesRepository.findByTelegramFileGroup(telegramFileGroupId);
            if (!files.isEmpty() && !fileId.isEmpty()) {
                var history = files.getFirst().getHistory();
                var newFile = buildHistoryFile(
                        fileId,
                        telegramFileGroupId,
                        history,
                        user,
                        historyFileType);

                historyFilesRepository.save(newFile);
                return;
            }

            createNewHistory(
                    caption,
                    fileId,
                    chatId,
                    user,
                    telegramFileGroupId,
                    historyFileType);
            return;
        }

        createNewHistory(
                caption,
                fileId,
                chatId,
                user,
                telegramFileGroupId,
                historyFileType);

    }

    private HistoryFile buildHistoryFile(String fileId, String telegramFileGroupId, History history, User user, HistoryFileType historyFileType) {
        return HistoryFile.builder()
                .fileType(historyFileType)
                .telegramFileGroup(telegramFileGroupId)
                .history(history)
                .user(user)
                .createdAt(LocalDateTime.now())
                .telegramFileId(fileId)
                .build();
    }

    private void createNewHistory(String caption, String fileId, Long chatId, User user, String telegramFileGroupId, HistoryFileType historyFileType) {
        if (caption == null || caption.isEmpty()) {
            caption = "unknown";
        }

        var newHistory = History.builder()
                .historyStatus(HistoryStatus.AWAITING)
                .description(caption)
                .approve(false)
                .amount(0.)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .currency(currency)
                .build();

        newHistory = historyRepository.save(newHistory);

        if (!fileId.isEmpty()) {
            var newFile = HistoryFile.builder()
                    .fileType(historyFileType)
                    .telegramFileGroup(telegramFileGroupId)
                    .history(newHistory)
                    .createdAt(LocalDateTime.now())
                    .user(user)
                    .telegramFileId(fileId)
                    .build();

            historyFilesRepository.save(newFile);
        }
        sendMessage(chatId, "История добавлена. Откройте список с историями, установите желаемую цену и нажмите опубликовать!");
    }

    private boolean checkValueToken(User user, Long chatId) {
        try {
            var currentTokenFromTonWallet = user.getWallet().getBalance() + openWalletCommand.getBalanceTonWallet(user.getWallet().getAccountId());

            if (currentTokenFromTonWallet < minTokenValue) {
                sendMessage(chatId,
                        "Общее количество токенов на балансе и TON кошельке, не должно быть меньше %.2f. Приобретите токены которые мы используем!".formatted(minTokenValue));
                return true;
            }
        } catch (TONAPIBadRequestError e) {
            log.error("Error getting balance from ton wallet!", e);
            sendMessage(chatId, "Что-то пошло не так, попробуйте повторить попытку!");
            return true;
        }
        return false;
    }

    private void sendMessage(Long chatId, String message) {
        try {
            telegramClient.execute(Sender.sendMessage(chatId, message));
        } catch (TelegramApiException e) {
            log.error("Error sending history message", e);
        }
    }
}
