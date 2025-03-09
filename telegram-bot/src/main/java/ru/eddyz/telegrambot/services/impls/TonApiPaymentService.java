package ru.eddyz.telegrambot.services.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.sync.Tonapi;
import ru.eddyz.telegrambot.domain.entities.Payment;
import ru.eddyz.telegrambot.repositories.PaymentRepository;
import ru.eddyz.telegrambot.services.PaymentService;

import java.util.List;



@Slf4j
@Service
@RequiredArgsConstructor
public class TonApiPaymentService implements PaymentService {

    private final Tonapi tonapi;
    private final PaymentRepository paymentRepository;



    @Override
    public void save(Payment payment) {

    }

    @Override
    public List<Payment> findByTelegramId(Long chatId) {
        return List.of();
    }
}
