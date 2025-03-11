package ru.eddyz.telegrambot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.OpenHistoryListCommand;
import ru.eddyz.telegrambot.commands.SetPaymentAmountCommand;
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
public class SetPaymentAmountCommandImpl implements SetPaymentAmountCommand {

    private final Map<Long, Long> editHistoryPaymentAmountIds = new HashMap<>();
    private final TelegramClient telegramClient;
    private final HistoryRepository historyRepository;
    private final OpenHistoryListCommand openHistoryListCommand;


    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();
        var dataSplit = callbackQuery.getData().split(":");

        try {
            var id = Long.parseLong(dataSplit[1]);
            editHistoryPaymentAmountIds.put(chatId, id);
            DataStore.currentCommand.put(chatId, ButtonsIds.HISTORY_PRICE_BUTTON.name());

            deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
            sendMessage(chatId, "Отправьте желаемую сумму: ");
        } catch (NumberFormatException e) {
            log.error("Invalid chat id or historyId {}", e.getMessage());
        }

    }

    @Override
    public void execute(Message message) {
        var chatId = message.getChatId();

        if (!editHistoryPaymentAmountIds.containsKey(chatId)) {
            sendMessage(chatId, "Что-то пошло не так, попробуйте выбрать заново историю, которой хотите установить цену.");
            return;
        }

        try {
            var amount = Double.parseDouble(message.getText());
            var id = editHistoryPaymentAmountIds.get(chatId);

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

            history.setAmount(amount);
            history.setUpdatedAt(LocalDateTime.now());
            historyRepository.save(history);
            openHistoryListCommand.execute(message);
            sendMessage(chatId, "Цена успешно установлена!");

            editHistoryPaymentAmountIds.remove(chatId);
            DataStore.currentCommand.remove(chatId);
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Вводите только цифры! Повторите попытку:");
        }

    }

    private void deleteMessage(Long chatId, Integer messageId) {
        try {
            telegramClient.execute(DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build());
        } catch (TelegramApiException e) {
            log.error("error deleting message to SetPaymentAmountCommand", e);
        }
    }

    private void sendMessage(Long chatId, String message) {
        try {
            telegramClient.execute(Sender.sendMessage(chatId, message));
        } catch (TelegramApiException e) {
            log.error("Sending chat id {} failed", chatId, e);
        }
    }

}
