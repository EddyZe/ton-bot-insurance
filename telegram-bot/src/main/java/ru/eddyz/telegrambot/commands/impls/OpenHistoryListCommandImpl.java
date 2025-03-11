package ru.eddyz.telegrambot.commands.impls;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.OpenHistoryListCommand;
import ru.eddyz.telegrambot.domain.entities.History;
import ru.eddyz.telegrambot.domain.entities.HistoryFile;
import ru.eddyz.telegrambot.repositories.HistoryRepository;
import ru.eddyz.telegrambot.util.DataStore;
import ru.eddyz.telegrambot.util.InlineKey;
import ru.eddyz.telegrambot.util.Sender;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class OpenHistoryListCommandImpl implements OpenHistoryListCommand {

    private final TelegramClient telegramClient;
    private final HistoryRepository historyRepository;
    private final InlineKey inlineKey;


    @Override
    @Transactional
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getFrom().getId();

        int currentPage = currentPage(chatId);

        var pageable = getPageable(currentPage);
        var histories = historyRepository.findByTelegramChatId(chatId, pageable);
        if (histories.isEmpty()) {
            sendMessage(chatId, "Список ваших историй пуст!");
            return;
        }

        int totalPages = histories.getTotalPages();

        if (totalPages < currentPage) {
            currentPage = totalPages;
            DataStore.currentPageHistoryInsurance.put(chatId, currentPage);
        }


        openHistories(histories, chatId, totalPages, currentPage);
    }

    @Override
    @Transactional
    public void execute(Message message) {
        var chatId = message.getChatId();

        int currentPage = currentPage(chatId);

        var pageable = getPageable(currentPage);
        var histories = historyRepository.findByTelegramChatId(chatId, pageable);
        if (histories.isEmpty()) {
            sendMessage(chatId, "Список ваших историй пуст!");
            return;
        }

        int totalPages = histories.getTotalPages();

        if (totalPages < currentPage) {
            currentPage = totalPages;
            DataStore.currentPageHistorySurcharge.put(chatId, currentPage);
        }

        openHistories(histories, chatId, totalPages, currentPage);

    }

    private void openHistories(Page<History> histories, Long chatId, int totalPages, int currentPage) {
        for (History history : histories.stream().toList()) {
            var files = history.getFiles() == null ? new ArrayList<HistoryFile>() : history.getFiles();

            if (files.isEmpty()) {
                sendTextHistory(history, chatId, totalPages, currentPage);
            } else if (files.size() == 1) {
                var type = files.getFirst().getFileType();

                switch (type) {
                    case PHOTO -> sendNonMediaGroupPhoto(history, chatId, files.getFirst(), totalPages, currentPage);
                    case VIDEO -> sendNonMediaGroupVideo(history, chatId, files.getFirst(), totalPages, currentPage);
                    case DOCUMENT ->
                            sendNonMediaGroupDocument(history, chatId, files.getFirst(), totalPages, currentPage);
                }
            } else {
                sendMediaGroupFiles(history, chatId, files, currentPage, totalPages);
            }
        }
    }

    private void sendMediaGroupFiles(History history, Long chatId, List<HistoryFile> files, int currentPage, int totalPages) {
        for (HistoryFile f : files) {
            var type = f.getFileType();
            switch (type) {
                case PHOTO -> sendNonMediaGroupPhoto(history, chatId, f, totalPages, currentPage);
                case VIDEO -> sendNonMediaGroupVideo(history, chatId, f, totalPages, currentPage);
                case DOCUMENT -> sendNonMediaGroupDocument(history, chatId, f, totalPages, currentPage);
            }
            break;
        }
    }

    private void sendNonMediaGroupDocument(History history, Long chatId, HistoryFile file, int totalPages, int currentPage) {
        try {
            telegramClient.execute(SendDocument.builder()
                    .chatId(chatId)
                    .document(new InputFile(file.getTelegramFileId()))
                    .caption(generateMessage(history))
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(inlineKey.historyList(totalPages, currentPage, history.getId()))
                    .build()
            );
        } catch (TelegramApiException e) {
            log.error("Error sending document OpenHistoryCommand", e);
        }
    }

    private void sendNonMediaGroupVideo(History history, Long chatId, HistoryFile file, int totalPages, int currentPage) {
        try {
            telegramClient.execute(SendVideo.builder()
                    .chatId(chatId)
                    .video(new InputFile(file.getTelegramFileId()))
                    .caption(generateMessage(history))
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(inlineKey.historyList(totalPages, currentPage, history.getId()))
                    .build()
            );
        } catch (TelegramApiException e) {
            log.error("Error sending video OpenHistoryCommand", e);
        }
    }

    private void sendNonMediaGroupPhoto(History history, Long chatId, HistoryFile file, int totalPages, int currentPage) {
        try {
            telegramClient.execute(SendPhoto.builder()
                    .chatId(chatId)
                    .caption(generateMessage(history))
                    .photo(new InputFile(file.getTelegramFileId()))
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(inlineKey.historyList(totalPages, currentPage, history.getId()))
                    .build()
            );
        } catch (TelegramApiException e) {
            log.error("Error sending photo OpenHistoryCommand", e);
        }
    }

    private void sendTextHistory(History history, Long chatId, int totalPages, int currentPage) {
        try {
            telegramClient.execute(SendMessage.builder()
                    .chatId(chatId)
                    .parseMode(ParseMode.HTML)
                    .text(generateMessage(history))
                    .replyMarkup(inlineKey.historyList(totalPages, currentPage, history.getId()))
                    .build()
            );
        } catch (TelegramApiException e) {
            log.error("Error sending photo OpenHistoryCommand", e);
        }
    }

    @Override
    public void nextPage(Long chatId) {

        int currentPage = currentPage(chatId);
        currentPage++;

        DataStore.currentPageHistorySurcharge.put(chatId, currentPage);
    }

    @Override
    public void prevPage(Long chatId) {

        int currentPage = currentPage(chatId);
        currentPage--;
        if (currentPage < 0)
            currentPage = 0;

        DataStore.currentPageHistorySurcharge.put(chatId, currentPage);
    }

    private Integer currentPage(Long chatId) {
        int currentPage;
        if (!DataStore.currentPageHistorySurcharge.containsKey(chatId)) {
            DataStore.currentPageHistorySurcharge.put(chatId, 0);
        }

        currentPage = DataStore.currentPageHistorySurcharge.get(chatId);
        return currentPage;
    }

    private void sendMessage(Long chatId, String message) {
        try {
            telegramClient.execute(Sender.sendMessage(chatId, message));
        } catch (TelegramApiException e) {
            log.error("Error sending message OpenHistoryListCommand", e);
        }
    }

    private Pageable getPageable(int currentPage) {
        return PageRequest.of(currentPage, 1, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private String generateMessage(History history) {
        var dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return """
                <i>%s</i>
                
                <b>• Статус</b>: %s
                <b>• Желаемая стоимость</b>: %.2f %s
                <b>• Дата создания:</b> %s
                <b>• Дата последнего обновления</b>: %s
                """.formatted(
                history.getDescription(),
                history.getHistoryStatus().toString(),
                history.getAmount(),
                history.getCurrency(),
                history.getCreatedAt().format(dtf),
                history.getUpdatedAt().format(dtf)
        );
    }
}
