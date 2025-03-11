package ru.eddyz.telegrambot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.PublishHistoryCommand;
import ru.eddyz.telegrambot.domain.entities.History;
import ru.eddyz.telegrambot.domain.entities.HistoryFile;
import ru.eddyz.telegrambot.domain.enums.HistoryStatus;
import ru.eddyz.telegrambot.repositories.HistoryRepository;
import ru.eddyz.telegrambot.util.InlineKey;
import ru.eddyz.telegrambot.util.Sender;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Slf4j
@Component
@RequiredArgsConstructor
public class PublishHistoryCommandImpl implements PublishHistoryCommand {

    private final HistoryRepository historyRepository;
    private final TelegramClient telegramClient;
    private final InlineKey inlineKey;

    @Value("${telegram.group_telegram_id}")
    private Long groupId;

    @Value("${insurance.vote_period}")
    private Integer votePeriod;

    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        var splitData = callbackQuery.getData().split(":");
        answerCallback(callbackQuery.getId());
        try {
            var id = Long.parseLong(splitData[1]);
            var historyOp = historyRepository.findById(id);

            if (historyOp.isEmpty()) {
                sendMessage(chatId, "❗ Невозможно опубликовать историю. Возможно она была удалена!", null);
                return;
            }

            var history = historyOp.get();

            if (history.getHistoryStatus() != HistoryStatus.AWAITING_PUBLISH) {
                sendMessage(chatId, "❗ Невозможно опубликовать историю, так как она уже была опубликована или отклонена", null);
                return;
            }

            if (history.getAmount() < 1) {
                sendMessage(chatId, "❗ Невозможно опубликовать историю, цена которой равна 0", null);
                return;
            }

            sendHistory(history.getId(), chatId);
        } catch (NumberFormatException e) {
            log.error("Error parsing to number from string PublishHistoryCommandImpl", e);
        }
    }

    private void sendHistory(Long historyId, Long chatId) {
        Thread.startVirtualThread(() -> {
            var historyOp = historyRepository.findById(historyId);

            if (historyOp.isEmpty()) {
                sendMessage(chatId, "Что-то пошло не так! Возможно история была удалена!", null);
                return;
            }

            var history = historyOp.get();

            try {
                sendHistory(history);
            } catch (Exception e) {
                sendMessage(chatId, "Произошла ошибка при публикации истории. Попробуйте повторить попытку!", null);
                return;
            }

            history.setUpdatedAt(LocalDateTime.now());
            history.setHistoryStatus(HistoryStatus.PUBLISH);
            historyRepository.save(history);
            sendMessage(chatId, "🎉 История опубликована!", null);
            try {
                Thread.sleep(Duration.of(votePeriod, ChronoUnit.MINUTES));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            sendMessage(chatId, generateResultMessage(history),
                    inlineKey.resultVotes(historyId));
            history.setHistoryStatus(HistoryStatus.AWAITING_APPROVED);
            history.setUpdatedAt(LocalDateTime.now());
            historyRepository.save(history);
        });
    }

    private String generateResultMessage(History history) {
        return """
                ❗ Голосование было завершено!
                
                <i>%s</i>
                
                <b>• Желаемая сумма:</b> %s %s
                
                История отправлена на проверку администрации! Вы получите уведомление, когда администрация ее обработает!""".formatted(
                        history.getDescription(),
                        history.getAmount(),
                        history.getCurrency()
        );
    }

    private void sendHistory(History history) throws Exception {
        if (history.getFiles() == null || history.getFiles().isEmpty()) {
            sendMessage(groupId, generateGroupMessage(history), inlineKey.votes(history.getId()));
            return;
        }

        for (HistoryFile historyFile : history.getFiles()) {
            var type = historyFile.getFileType();
            switch (type) {
                case PHOTO -> telegramClient.execute(SendPhoto.builder()
                        .photo(new InputFile(historyFile.getTelegramFileId()))
                        .chatId(groupId)
                        .caption(generateGroupMessage(history))
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(inlineKey.votes(history.getId()))
                        .build());
                case VIDEO -> telegramClient.execute(
                        SendVideo.builder()
                                .chatId(groupId)
                                .caption(generateGroupMessage(history))
                                .parseMode(ParseMode.HTML)
                                .replyMarkup(inlineKey.votes(history.getId()))
                                .video(new InputFile(historyFile.getTelegramFileId()))
                                .build());
                case DOCUMENT -> telegramClient.execute(
                        SendDocument.builder()
                                .chatId(groupId)
                                .parseMode(ParseMode.HTML)
                                .replyMarkup(inlineKey.votes(history.getId()))
                                .caption(generateGroupMessage(history))
                                .document(new InputFile(historyFile.getTelegramFileId()))
                                .build()
                );
            }
            break;
        }
    }

    private void answerCallback(String id) {
        try {
            telegramClient.execute(new AnswerCallbackQuery(id));
        } catch (TelegramApiException e) {
            log.error("Error answering callback", e);
        }
    }

    private String generateGroupMessage(History history) {
        return """
                <i>%s</i>
                
                <b>• Автор:</b> %s
                <b>• Желаемая сумма</b>: %.2f %s
                
                Если вы захотите посмотреть дополнительные файлы, то они будут отправлены вам в ЛС.
                """.formatted(
                history.getDescription(),
                history.getUser().getUsername(),
                history.getAmount(), history.getCurrency()
        );
    }

    private void sendMessage(Long chatId, String message, InlineKeyboardMarkup keyboardMarkup) {
        try {
            var sendMessage = keyboardMarkup == null ?
                    Sender.sendMessage(chatId, message) :
                    Sender.sendMessage(chatId, message, keyboardMarkup);

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error sendMessage PublishHistoryCommandImpl", e);
        }
    }
}
