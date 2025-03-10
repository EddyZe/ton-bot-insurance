package ru.eddyz.telegrambot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.ShowInsuranceCommand;
import ru.eddyz.telegrambot.domain.entities.Insurance;
import ru.eddyz.telegrambot.repositories.InsuranceRepository;
import ru.eddyz.telegrambot.util.InlineKey;
import ru.eddyz.telegrambot.util.Sender;

import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShowInsuranceCommandImpl implements ShowInsuranceCommand {

    private final InsuranceRepository insuranceRepository;
    private final TelegramClient telegramClient;
    private final InlineKey inlineKey;


    @Override
    public void execute(Message message) {
        var chatId = message.getChatId();

        var insuranceOp = insuranceRepository.findByChatIdIsActive(chatId);
        if (insuranceOp.isEmpty()) {
            sendMessage(chatId, "На данный момент у вас нет купленной страховки!", inlineKey.insuranceMenu());
            return;
        }

        var insurance = insuranceOp.get();

        sendMessage(chatId, generateMessage(insurance), inlineKey.insuranceMenu());
    }

    private void sendMessage(Long chatId, String message, InlineKeyboardMarkup keyboard) {
        try {
            telegramClient.execute(
                    Sender.sendMessage(chatId, message, keyboard)
            );
        } catch (TelegramApiException e) {
            log.error("error sendMessage to ShowInsuranceCommand", e);
        }
    }

    private String generateMessage(Insurance insurance) {
        var dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return """
                <b>Ваша страховка: </b>
                
                <b>Цена</b>: %.2f %s
                <b>Дата покупки:</b> %s
                <b>Дата окончания: </b> %s
                """.formatted(
                insurance.getAmount(),
                insurance.getCurrency(),
                insurance.getStartDate().format(dtf),
                insurance.getEndDate().format(dtf)
        );
    }
}
