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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.HistoryInsuranceCommand;
import ru.eddyz.telegrambot.domain.entities.Insurance;
import ru.eddyz.telegrambot.domain.entities.Payment;
import ru.eddyz.telegrambot.repositories.InsuranceRepository;
import ru.eddyz.telegrambot.util.DataStore;
import ru.eddyz.telegrambot.util.InlineKey;

import java.time.format.DateTimeFormatter;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryInsuranceCommandImpl implements HistoryInsuranceCommand {

    private final InsuranceRepository insuranceRepository;
    private final TelegramClient telegramClient;

    private final InlineKey inlineKey;


    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getFrom().getId();
        int currentPage = currentPage(chatId);

        Pageable pageable = PageRequest.of(currentPage, 5, Sort.by(Sort.Direction.DESC, "endDate"));


        var pages = insuranceRepository.findByChatId(chatId, pageable);
        int totalPages = pages.getTotalPages();

        if (totalPages < currentPage) {
            currentPage = totalPages;
            DataStore.currentPageHistoryInsurance.put(chatId, currentPage);
        }

        var editMessage = EditMessageText.builder()
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(generateMessage(pages.stream().toList()))
                .chatId(chatId)
                .parseMode(ParseMode.HTML)
                .replyMarkup(inlineKey.insuranceHistory(totalPages, currentPage))
                .build();

        try {
            telegramClient.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("error sendMessage to HistoryPaymentsImpl", e);
        }
    }

    private Integer currentPage(Long chatId) {
        int currentPage;
        if (!DataStore.currentPageHistoryInsurance.containsKey(chatId)) {
            DataStore.currentPageHistoryInsurance.put(chatId, 0);
        }

        currentPage = DataStore.currentPageHistoryInsurance.get(chatId);
        return currentPage;
    }

    private String generateMessage(List<Insurance> insurances) {
        var dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var message = new StringBuilder("<b>История купленных страховок: </b>\n\n");
        insurances.forEach(insurance -> {
            var temp = """
                    <b>Цена</b>: %.2f %s
                    <b>Дата покупки</b>: %s
                    <b>Дата окончания</b>: %s
                    -----------------------------------
                    """.formatted(
                    insurance.getAmount(),
                    insurance.getCurrency(),
                    insurance.getStartDate().format(dtf),
                    insurance.getEndDate().format(dtf)
            );

            message.append(temp);
        });

        return message.toString();
    }


    @Override
    public void nextPage(Long chatId) {

        int currentPage = currentPage(chatId);
        currentPage++;

        DataStore.currentPageHistoryInsurance.put(chatId, currentPage);
    }

    @Override
    public void prevPage(Long chatId) {

        int currentPage = currentPage(chatId);
        currentPage--;
        if (currentPage < 0)
            currentPage = 0;

        DataStore.currentPageHistoryInsurance.put(chatId, currentPage);
    }
}
