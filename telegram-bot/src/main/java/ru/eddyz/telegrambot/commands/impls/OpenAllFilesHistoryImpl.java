package ru.eddyz.telegrambot.commands.impls;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaDocument;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.OpenAllFilesHistory;
import ru.eddyz.telegrambot.domain.entities.HistoryFile;
import ru.eddyz.telegrambot.repositories.HistoryRepository;
import ru.eddyz.telegrambot.util.Sender;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAllFilesHistoryImpl implements OpenAllFilesHistory {

    private final TelegramClient telegramClient;
    private final HistoryRepository historyRepository;


    @Override
    @Transactional
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getFrom().getId();
        var dataSplit = callbackQuery.getData().split(":");

        answerCallBack(callbackQuery.getId());

        try {
            var id = Long.parseLong(dataSplit[1]);
            var hOp = historyRepository.findById(id);
            if (hOp.isEmpty()) {
                sendMessage(chatId, "История не была найдена! Возможно она была удалена!");
                return;
            }

            var history = hOp.get();

            if (history.getFiles() == null || history.getFiles().isEmpty()) {
                sendMessage(chatId, "Данная история не содержит файлов!");
                return;
            }

            var files = history.getFiles();

            if (files.size() < 2) {
                sendMessage(chatId, "В данной истории только 1 файл!");
                return;
            }

            sendMediaGroupFiles(chatId, callbackQuery.getMessage().getMessageId(), files);

        } catch (NumberFormatException e) {
            log.error("Error number format from String OpenAllFilesHistoryImpl", e);
        }

    }

    private void answerCallBack(String id) {
        try {
            telegramClient.execute(new AnswerCallbackQuery(id));
        } catch (TelegramApiException e) {
            log.error("Error answer call back to ListFilesHistory", e);
        }
    }

    private void sendMessage(Long chatId, String message) {
        try {
            telegramClient.execute(Sender.sendMessage(chatId, message));
        } catch (TelegramApiException e) {
            log.error("error send message to OpenAllFilesHistoryImpl", e);
        }
    }

    private void sendMediaGroupFiles(Long chatId, Integer messageId, List<HistoryFile> files) {
        var mediaList = files.stream()
                .map(f -> {
                    var type = f.getFileType();
                    switch (type) {
                        case PHOTO -> {
                            return InputMediaPhoto.builder()
                                    .parseMode(ParseMode.HTML)
                                    .media(f.getTelegramFileId())
                                    .build();
                        }
                        case VIDEO -> {

                            return InputMediaVideo.builder()
                                    .parseMode(ParseMode.HTML)
                                    .media(f.getTelegramFileId())
                                    .build();
                        }
                        case DOCUMENT -> {

                            return InputMediaDocument.builder()
                                    .parseMode(ParseMode.HTML)
                                    .media(f.getTelegramFileId())
                                    .build();
                        }
                    }
                    return null;
                })
                .toList();
        var sendMedia = SendMediaGroup.builder()
                .chatId(chatId)
                .medias(mediaList)
//                .replyParameters(ReplyParameters.builder()
//                        .chatId(chatId)
//                        .messageId(messageId)
//                        .build())
                .build();

        try {
            telegramClient.execute(sendMedia);
        } catch (TelegramApiException e) {
            log.error("Error sending media OpenHistoryCommand", e);
        }
    }
}
