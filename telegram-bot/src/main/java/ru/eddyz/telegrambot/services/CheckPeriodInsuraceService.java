package ru.eddyz.telegrambot.services;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.repositories.InsuranceRepository;
import ru.eddyz.telegrambot.util.Sender;

import java.time.LocalDate;


@Slf4j
@Service
@RequiredArgsConstructor
public class CheckPeriodInsuraceService {

    private final InsuranceRepository insuranceRepository;
    private final TelegramClient telegramClient;

    @Transactional
    @Scheduled(cron = "0 0 6 * * *")
    public void checkPeriod() {
        var isurances = insuranceRepository.findByActive(true);

        isurances.forEach(insurance -> {
            var currentDate = LocalDate.now();
            var endDate = insurance.getEndDate();

            if (currentDate.isAfter(endDate.toLocalDate())) {
                try {
                    telegramClient.execute(Sender.sendMessage(insurance.getUser().getTelegramChatId(), "Срок действия страховки истек! Не забудьте приобрести новую!"));
                } catch (TelegramApiException e) {
                    log.error("error sending message to checkPeriodInsurace", e);
                }

                insurance.setActive(false);
                insuranceRepository.save(insurance);
            }

        });
    }


}
