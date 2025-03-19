package ru.eddyz.telegrambot.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.domain.enums.HistoryStatus;
import ru.eddyz.telegrambot.repositories.HistoryRepository;
import ru.eddyz.telegrambot.util.InlineKey;
import ru.eddyz.telegrambot.util.Sender;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckOldHistoryRepository {

    private final TelegramClient telegramClient;
    private final HistoryRepository historyRepository;
    private final PublisherService publisherService;
    private final InlineKey inlineKey;

    @Value("${insurance.vote_period}")
    private Integer period;

    @Transactional
    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    public void checkOldHistory() {
        historyRepository.findByHistoryStatus(HistoryStatus.PUBLISH)
                .forEach(history -> {
                    var endDate = LocalDateTime.now().minusMinutes(period);

                    if (endDate.isAfter(history.getUpdatedAt())) {
                        history.setUpdatedAt(endDate);
                        history.setHistoryStatus(HistoryStatus.AWAITING_APPROVED);
                        historyRepository.save(history);

                        try {
                            telegramClient.execute(
                                    Sender.sendMessage(
                                            history.getUser().getTelegramChatId(),
                                            publisherService.generateResultMessage(history),
                                            inlineKey.resultVotes(history.getId())));
                        } catch (TelegramApiException e) {
                            log.error(e.getMessage());
                        }
                    }
                });
    }

}
