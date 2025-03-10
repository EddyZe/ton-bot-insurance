package ru.eddyz.telegrambot.commands.impls;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.HistoryWithdrawCommand;
import ru.eddyz.telegrambot.domain.entities.Withdraw;
import ru.eddyz.telegrambot.repositories.WithdrawRepository;
import ru.eddyz.telegrambot.util.DataStore;
import ru.eddyz.telegrambot.util.InlineKey;
import ru.eddyz.telegrambot.util.Sender;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryWithdrawCommandImpl implements HistoryWithdrawCommand {

    private final WithdrawRepository withdrawRepository;

    private final TelegramClient telegramClient;
    private final InlineKey inlineKey;


    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getMessage().getChatId();

        int currentPage = currentPage(chatId);

        Pageable pageable = PageRequest.of(currentPage, 5,  Sort.by(Sort.Direction.DESC, "createdAt"));

        var pages = withdrawRepository.findByTelegramChatId(chatId, pageable);

        int totalPages = pages.getTotalPages();

        if (totalPages < currentPage) {
            currentPage = totalPages;
            DataStore.currentPageHistoryWithdraw.put(chatId, currentPage);
        }

        var editMessage = EditMessageText.builder()
                .text(generateMessage(pages.stream().toList()))
                .messageId(callbackQuery.getMessage().getMessageId())
                .chatId(chatId)
                .parseMode(ParseMode.HTML)
                .replyMarkup(inlineKey.withdrawHistory(totalPages, currentPage))
                .build();

        try {
            telegramClient.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("error sendMessage to HistoryWithdrawCommandImpl", e);
        }

        answerCallback(callbackQuery.getId());
    }

    private void answerCallback(String id) {
        try {
            telegramClient.execute(new AnswerCallbackQuery(id));
        } catch (TelegramApiException e) {
            log.error("error answerCallback to HistoryWithdrawCommandImpl", e);
        }
    }

    private String generateMessage(List<Withdraw> withdraws) {
        StringBuilder message = new StringBuilder("История ваших снятий: \n\n");
        var dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        withdraws.forEach(withdraw -> {
            var temp = """
                    <b>ID платежа</b>: %d
                    <b>Сумма</b>: %.2f %s
                    <b>Дата</b>: %s
                    <b>Статус</b>: %s
                    ---------------------------
                    """.formatted(
                    withdraw.getId(),
                    withdraw.getAmount(),
                    withdraw.getToken(),
                    dtf.format(withdraw.getCreatedAt()),
                    withdraw.getStatus().toString());

            message.append(temp);
        });
        return message.toString();
    }

    public Integer currentPage(Long chatId) {
        int currentPage = 0;
        if (!DataStore.currentPageHistoryWithdraw.containsKey(chatId)) {
            DataStore.currentPageHistoryWithdraw.put(chatId, 0);
        }

        currentPage = DataStore.currentPageHistoryWithdraw.get(chatId);
        return currentPage;
    }

    public void nextPage(Long chaId) {
        int currentPage = currentPage(chaId);
        currentPage++;

        DataStore.currentPageHistoryWithdraw.put(chaId, currentPage);
    }

    public void prevPage(Long chatId) {
        int currentPage = currentPage(chatId);
        currentPage--;
        if (currentPage < 0)
            currentPage = 0;

        DataStore.currentPageHistoryWithdraw.put(chatId, currentPage);
    }
}
