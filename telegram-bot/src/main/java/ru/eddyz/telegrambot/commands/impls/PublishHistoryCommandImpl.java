package ru.eddyz.telegrambot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.PublishHistoryCommand;
import ru.eddyz.telegrambot.domain.enums.HistoryStatus;
import ru.eddyz.telegrambot.repositories.HistoryRepository;
import ru.eddyz.telegrambot.util.Sender;


@Slf4j
@Component
@RequiredArgsConstructor
public class PublishHistoryCommandImpl implements PublishHistoryCommand {

    private final HistoryRepository historyRepository;
    private final TelegramClient telegramClient;


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

            history.setHistoryStatus(HistoryStatus.ADMIN_CHECKING);
            historyRepository.save(history);
            sendMessage(chatId, "История отправлена на проверку. Вам придет уведомление, после принятия решения!", null);
        } catch (NumberFormatException e) {
            log.error("Error parsing to number from string PublishHistoryCommandImpl", e);
        }
    }

    private void answerCallback(String id) {
        try {
            telegramClient.execute(new AnswerCallbackQuery(id));
        } catch (TelegramApiException e) {
            log.error("Error answering callback", e);
        }
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
