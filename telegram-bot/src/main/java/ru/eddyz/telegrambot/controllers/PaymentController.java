package ru.eddyz.telegrambot.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.clients.tonapi.ton.exception.TONAPIBadRequestError;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.events.Event;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.events.action.Action;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.transactions.Transaction;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.sync.Tonapi;
import ru.eddyz.telegrambot.domain.entities.Payment;
import ru.eddyz.telegrambot.domain.payloads.PaymentPayload;
import ru.eddyz.telegrambot.repositories.PaymentRepository;
import ru.eddyz.telegrambot.repositories.UserRepository;
import ru.eddyz.telegrambot.repositories.WalletRepository;
import ru.eddyz.telegrambot.util.Sender;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("payment")
public class PaymentController {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    private final Tonapi tonapi;
    private final TelegramClient telegramClient;
    private final WalletRepository walletRepository;

    @Value("${insurance.token.name}")
    private String tokenName;

    @Value("${telegram.bot_token}")


    @PostMapping("/{botToken}")
    public ResponseEntity<?> payment(@RequestBody PaymentPayload paymentPayload, @PathVariable String botToken) {
        if (botToken == null || botToken.isEmpty() || !botToken.equals(tokenName))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "invalid token"));

        try {
            var resp = tonapi.getBlockchain().getTransactionData(paymentPayload.getTxHash());
            var chatIdUser = getCommentFromTransfer(resp);

            if (chatIdUser.isEmpty()) {
                log.error("chatIdUser is null");
            } else {
                var chatIdCast = Long.parseLong(chatIdUser);
                var userOp = userRepository.findByTelegramChatId(chatIdCast);

                if (userOp.isPresent()) {
                    var jet = tonapi.getJettons().getJettonTransferEvent(resp.getHash());
                    var actionOp = getActionFromTransferJettons(jet);

                    if (actionOp.isPresent()) {
                        var action = actionOp.get();
                        var wallet = walletRepository.findByUserTelegramId(chatIdCast);
                        if (wallet.isPresent()) {
                            var wal = wallet.get();
                            var currentBalance = wal.getBalance();
                            var upBalance = action.getJettonTransfer().getAmount().doubleValue() / 1_000_000_000;
                            var newBalance = currentBalance + upBalance;
                            wal.setBalance(newBalance);
                            walletRepository.save(wal);

                            var sendMessage = Sender.sendMessage(chatIdCast, "Вам начислено %.2f %s".formatted(upBalance, tokenName));

                            try {
                                telegramClient.execute(sendMessage);
                            } catch (TelegramApiException e) {
                                log.error("Error sendMessage to upBalance");
                            }

                            var payment = Payment.builder()
                                    .amount(upBalance)
                                    .hash(resp.getHash())
                                    .user(userOp.get())
                                    .createdAt(LocalDateTime.now())
                                    .currency(tokenName)
                                    .status(true)
                                    .build();

                            paymentRepository.save(payment);
                        }
                    }
                }
            }
        } catch (TONAPIBadRequestError e) {
            log.error("TONAPIBadRequestError ", e);
        }


        return ResponseEntity.ok(HttpStatus.OK);
    }

    private String getCommentFromTransfer(Transaction transaction) {
        var chatIdUser = Optional.ofNullable(transaction.getInMsg().getDecodedBody().get("text")).orElse("");

        if (chatIdUser.toString().isEmpty()) {
            try {
                var forwardPayloadData = Optional.ofNullable(transaction.getInMsg().getDecodedBody().get("forward_payload"))
                        .orElseGet(HashMap::new);

                if (forwardPayloadData instanceof Map<?, ?> forwardPayload) {
                    var value = forwardPayload.get("value");

                    if (value instanceof Map<?, ?> valueMap) {
                        var innerValue = valueMap.get("value");

                        if (innerValue instanceof Map<?, ?> innerValueMap) {
                            var text = innerValueMap.get("text");

                            if (text instanceof String) {
                                chatIdUser = text;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                chatIdUser = "";
            }
        }

        return chatIdUser.toString();
    }

    private Optional<Action> getActionFromTransferJettons(Event transaction) {
        var jet = transaction.getActions();

        for (Action action : jet) {
            if (action.getType().equals("JettonTransfer") && action.getJettonTransfer().getJetton().getName().equals(tokenName)) {
                return Optional.of(action);
            }
        }

        return Optional.empty();
    }
}
