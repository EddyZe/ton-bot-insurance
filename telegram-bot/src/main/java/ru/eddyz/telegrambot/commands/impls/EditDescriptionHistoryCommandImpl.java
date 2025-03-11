package ru.eddyz.telegrambot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.EditDescriptionHistoryCommand;
import ru.eddyz.telegrambot.commands.OpenHistoryListCommand;
import ru.eddyz.telegrambot.domain.enums.ButtonsIds;
import ru.eddyz.telegrambot.domain.enums.HistoryStatus;
import ru.eddyz.telegrambot.repositories.HistoryRepository;
import ru.eddyz.telegrambot.util.DataStore;
import ru.eddyz.telegrambot.util.Sender;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class EditDescriptionHistoryCommandImpl implements EditDescriptionHistoryCommand {

    private final HistoryRepository historyRepository;
    private final TelegramClient telegramClient;
    private final OpenHistoryListCommand openHistoryListCommand;
    private final Map<Long, Long> currentHistoryEditDescriptionIds = new HashMap<>();


    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        var splitData = callbackQuery.getData().split(":");

        try {
            var id = Long.parseLong(splitData[1]);
            DataStore.currentCommand.put(chatId, ButtonsIds.HISTORY_EDIT_BUTTON.name());
            currentHistoryEditDescriptionIds.put(chatId, id);

            sendMessage(chatId, "Введите новый текст истории: ");
            deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        } catch (NumberFormatException e) {
            log.error("Error parse to number from is string EditDescriptionHistoryCommandImpl", e);
        }

    }

    @Override
    public void execute(Message message) {
        var chatId = message.getChatId();

        if (!currentHistoryEditDescriptionIds.containsKey(chatId)) {
            sendMessage(chatId, "Что-то пошло не так, попробуйте выбрать заново историю, которой хотите установить цену.");
            return;
        }

        var id = currentHistoryEditDescriptionIds.get(chatId);

        var historyOp = historyRepository.findById(id);
        if (historyOp.isEmpty()) {
            sendMessage(chatId, "Что-то пошло не так! Возможно история была удалена!");
            return;
        }

        var history = historyOp.get();

        if (history.getHistoryStatus() != HistoryStatus.AWAITING_PUBLISH) {
            sendMessage(chatId, "❗ История уже была опубликована! Цену после публикации изменить нельзя!");
            openHistoryListCommand.execute(message);
            return;
        }

        history.setDescription(message.getText());
        history.setUpdatedAt(LocalDateTime.now());
        historyRepository.save(history);
        openHistoryListCommand.execute(message);
        sendMessage(chatId, "Цена успешно установлена!");

        currentHistoryEditDescriptionIds.remove(chatId);
        DataStore.currentCommand.remove(chatId);
    }

    private void sendMessage(Long chatId, String message) {
        try {
            telegramClient.execute(Sender.sendMessage(chatId, message));
        } catch (TelegramApiException e) {
            log.error("Error while sending edit-description history command", e);
        }
    }

    private void deleteMessage(Long chatId, Integer messageId) {
        try {
            telegramClient.execute(DeleteMessage
                    .builder()
                    .messageId(messageId)
                    .chatId(chatId)
                    .build());
        } catch (TelegramApiException e) {
            log.error("Error while sending edit-description history command", e);
        }
    }
}
