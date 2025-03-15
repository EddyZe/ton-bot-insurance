package ru.eddyz.telegrambot.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.domain.entities.History;
import ru.eddyz.telegrambot.domain.entities.HistoryFile;
import ru.eddyz.telegrambot.domain.enums.HistoryStatus;
import ru.eddyz.telegrambot.exception.PublisherException;
import ru.eddyz.telegrambot.repositories.HistoryRepository;
import ru.eddyz.telegrambot.util.InlineKey;
import ru.eddyz.telegrambot.util.Sender;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublisherService {


    private final TelegramClient telegramClient;
    private final HistoryRepository historyRepository;
    private final InlineKey inlineKey;


    @Value("${telegram.group_telegram_id}")
    private Long groupId;

    @Value("${insurance.vote_period}")
    private Integer votePeriod;

    public void publish(Long historyId, Long chatId) {
        Thread.startVirtualThread(() -> {
            var historyOp = historyRepository.findById(historyId);

            if (historyOp.isEmpty()) {
                throw new PublisherException("History not found");
            }

            var history = historyOp.get();

            if (history.getHistoryStatus() == HistoryStatus.DECLINE) {
                sendMessage(chatId, generateBadMessage(history), null);
                return;
            }

            try {
                sendHistory(history);
            } catch (Exception e) {
                throw new PublisherException(e);
            }

            history.setUpdatedAt(LocalDateTime.now());
            history.setHistoryStatus(HistoryStatus.PUBLISH);
            historyRepository.save(history);
            sendMessage(chatId, generatePublisherMessage(history), null);
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

    private String generatePublisherMessage(History history) {
        return """
                🎉 История опубликована!
                
                <i>%s</i>
                
                <b>• Желаемая сумма:</b> %.2f %s""".formatted(
                history.getDescription(),
                history.getAmount(),
                history.getCurrency()
        );
    }

    private String generateBadMessage(History history) {
        return """
                ❗ Объявление не прошло модерацию!
                
                <i>%s</i>
                
                <b>• Желаемая сумма:</b> %.2f %s
                
                К сожалению администрация отклонила вашу публикацию! 😢""".formatted(
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

    public String generateResultMessage(History history) {
        return """
                🎉 Голосование было завершено!
                
                <i>%s</i>
                
                <b>• Желаемая сумма:</b> %.2f %s
                
                ❗ История отправлена на одобрение!
                Вы получите уведомление, когда администрация ее обработает!
                Так же в группу будет отправлен результат!""".formatted(
                history.getDescription(),
                history.getAmount(),
                history.getCurrency()
        );
    }


}
