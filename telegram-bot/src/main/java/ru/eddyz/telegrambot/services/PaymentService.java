package ru.eddyz.telegrambot.services;


import ru.eddyz.telegrambot.domain.entities.Payment;

import java.util.List;

public interface PaymentService {


    void save(Payment payment);
    List<Payment> findByTelegramId(Long chatId);


}
