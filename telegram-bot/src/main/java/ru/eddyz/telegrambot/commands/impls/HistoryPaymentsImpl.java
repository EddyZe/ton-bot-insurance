package ru.eddyz.telegrambot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.HistoryPayments;
import ru.eddyz.telegrambot.domain.entities.Payment;
import ru.eddyz.telegrambot.repositories.PaymentRepository;
import ru.eddyz.telegrambot.util.DataStore;
import ru.eddyz.telegrambot.util.InlineKey;
import ru.eddyz.telegrambot.util.Sender;

import java.time.format.DateTimeFormatter;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryPaymentsImpl implements HistoryPayments {

    private final TelegramClient telegramClient;
    private final PaymentRepository paymentRepository;
    private final InlineKey inlineKey;


    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getFrom().getId();
        int currentPage = currentPage(chatId);
        var pageable = getPageable(currentPage);

        var pages = paymentRepository.findByTelegramId(chatId, pageable);
        int totalPages = pages.getTotalPages();

        if (totalPages < currentPage) {
            currentPage = totalPages;
            DataStore.currentPageHistoryPayments.put(chatId, currentPage);
        }

        var editMessage = EditMessageText.builder()
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(generateMessage(pages.stream().toList()))
                .chatId(chatId)
                .parseMode(ParseMode.HTML)
                .replyMarkup(inlineKey.paymentHistory(totalPages, currentPage))
                .build();

        try {
            telegramClient.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("error sendMessage to HistoryPaymentsImpl", e);
        }
    }

    @Override
    public void execute(Message message) {
        var chatId = message.getChatId();
        int currentPage = currentPage(chatId);
        var pageable = getPageable(currentPage);

        var pages = paymentRepository.findByTelegramId(chatId, pageable);
        int totalPages = pages.getTotalPages();

        if (totalPages < currentPage) {
            currentPage = totalPages;
            DataStore.currentPageHistoryPayments.put(chatId, currentPage);
        }

        var sendMessage = Sender.sendMessage(chatId, generateMessage(pages.stream().toList()), inlineKey.paymentHistory(totalPages, currentPage));

        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("error sendMessage to HistoryPaymentsImpl", e);
        }
    }

    private String generateMessage(List<Payment> payments) {
        var dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        var message = new StringBuilder("<b>История ваших платежей: </b>\n\n");
        payments.forEach(payment -> {
            var temp = """
                    <b>ID платежа</b>: %d
                    <b>Сумма</b>: %.2f %s
                    <b>Дата</b>: %s
                    -------------------------------
                    """.formatted(
                            payment.getId(),
                    payment.getAmount(),
                    payment.getCurrency(),
                    payment.getCreatedAt().format(dtf)
            );

            message.append(temp);
        });

        return message.toString();
    }

    private Pageable getPageable(int currentPage) {
        return PageRequest.of(currentPage, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private Integer currentPage(Long chatId) {
        int currentPage;
        if (!DataStore.currentPageHistoryPayments.containsKey(chatId)) {
            DataStore.currentPageHistoryPayments.put(chatId, 0);
        }

        currentPage = DataStore.currentPageHistoryPayments.get(chatId);
        return currentPage;
    }

    @Override
    public void nextPage(Long chatId) {

        int currentPage = currentPage(chatId);
        currentPage++;

        DataStore.currentPageHistoryPayments.put(chatId, currentPage);
    }

    @Override
    public void prevPage(Long chatId) {
        int currentPage = currentPage(chatId);
        currentPage--;
        if (currentPage < 0)
            currentPage = 0;

        DataStore.currentPageHistoryPayments.put(chatId, currentPage);
    }
}
