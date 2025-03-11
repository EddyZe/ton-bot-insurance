package ru.eddyz.telegrambot.commands.impls;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.DeleteHistoryCommand;
import ru.eddyz.telegrambot.commands.OpenHistoryListCommand;
import ru.eddyz.telegrambot.repositories.HistoryRepository;
import ru.eddyz.telegrambot.util.Sender;


@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteHistoryCommandImpl implements DeleteHistoryCommand {

    private final TelegramClient telegramClient;
    private final HistoryRepository historyRepository;
    private final OpenHistoryListCommand openHistoryListCommand;


    @Override
    @Transactional
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getFrom().getId();
        var dataSplit = callbackQuery.getData().split(":");

        try {
            var id = Long.parseLong(dataSplit[1]);
            historyRepository.deleteById(id);
            telegramClient.execute(DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .build());
            telegramClient.execute(Sender.sendMessage(chatId, "История успешно удалена!"));
            openHistoryListCommand.execute(callbackQuery);
        } catch (NumberFormatException | TelegramApiException e) {
            log.error("Error sending message OpenHistoryListCommand", e);
        }
    }
}
