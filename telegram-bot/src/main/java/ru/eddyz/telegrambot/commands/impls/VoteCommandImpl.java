package ru.eddyz.telegrambot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.VoteCommand;
import ru.eddyz.telegrambot.domain.entities.History;
import ru.eddyz.telegrambot.domain.entities.User;
import ru.eddyz.telegrambot.domain.entities.Vote;
import ru.eddyz.telegrambot.domain.enums.HistoryStatus;
import ru.eddyz.telegrambot.domain.enums.VotingSolution;
import ru.eddyz.telegrambot.repositories.HistoryRepository;
import ru.eddyz.telegrambot.repositories.UserRepository;
import ru.eddyz.telegrambot.repositories.VoteRepository;
import ru.eddyz.telegrambot.util.DataStore;
import ru.eddyz.telegrambot.util.Sender;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class VoteCommandImpl implements VoteCommand {

    private final TelegramClient telegramClient;
    private final HistoryRepository historyRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final Map<Long, Long> currentVoteHistoryId = new HashMap<>();


    @Override
    @Transactional
    public void vote(CallbackQuery callbackQuery, VotingSolution vote) {
        var userChatId = callbackQuery.getFrom().getId();
        var splitData = callbackQuery.getData().split(":");

        long historyId;

        try {
            historyId = Long.parseLong(splitData[1]);
        } catch (NumberFormatException e) {
            log.error("error parse to number from string", e);
            answerCallbackMessage(callbackQuery.getId(), "Что-то пошло не так. Попробуйте повторить попытку");
            return;
        }

        var history = historyRepository.findById(historyId);
        if (history.isEmpty()) {
            answerCallbackMessage(callbackQuery.getId(), "❗ Невозможно проголосовать за эту историю. Возможно она была удалена!");
            return;
        }

        if (history.get().getHistoryStatus() != HistoryStatus.PUBLISH) {
            answerCallbackMessage(callbackQuery.getId(), "❗ Голосование уже прошло!");
            return;
        }

        var user = userRepository.findByTelegramChatId(userChatId);
        if (user.isEmpty()) {
            sendMessage(userChatId, "Ваш аккаунт не активирован. Чтобы активировать аккаунт введите команду /start");
            return;
        }

        if (checkReplayVote(history.get(), userChatId)) {
            answerCallbackMessage(callbackQuery.getId(), "Вы уже отдали голос за эту историю!");
            return;
        }

        switch (vote) {
            case VOTING_NO, VOTING_YES -> {
                var newVote = Vote.builder()
                        .user(user.get())
                        .createdAt(LocalDateTime.now())
                        .history(history.get())
                        .amount(history.get().getAmount())
                        .solution(vote)
                        .build();

                voteRepository.save(newVote);
                answerCallbackMessage(callbackQuery.getId(), generateMessage(user.get()));
            }
            case VOTING_SET_PRICE_YES -> {
                DataStore.currentCommand.put(userChatId, VotingSolution.VOTING_SET_PRICE_YES.name());
                currentVoteHistoryId.put(userChatId, history.get().getId());
                answerCallbackMessage(callbackQuery.getId(), "Инструкции отправлены в ЛС. Следуйте инструкциям!");
                sendMessage(userChatId, "Отправьте цену, которую считаете приемлемой: ");
            }
        }
    }

    private void answerCallbackMessage(String id, String message) {
        try {
            telegramClient.execute(AnswerCallbackQuery.builder()
                    .text(message)
                    .callbackQueryId(id)
                    .cacheTime(5)
                    .build());
        } catch (TelegramApiException e) {
            log.error("error answer callback from VoteCommand", e);
        }
    }

    private boolean checkReplayVote(History history, Long userChatId) {
        for (Vote v : history.getVotes()) {
            if (v.getUser().getTelegramChatId().equals(userChatId)) {
                return true;
            }
        }
        return false;
    }

    private String generateMessage(User user) {
        return """
                ❗ %s, Ваш голос будет учтен!"""
                .formatted(user.getUsername());
    }

    private void sendMessage(Long chatId, String message) {
        try {
            telegramClient.execute(Sender.sendMessage(chatId, message));
        } catch (TelegramApiException e) {
            log.error("error send message to VoteCommand", e);
        }
    }

    @Override
    @Transactional
    public void execute(Message message) {
        var chatId = message.getChatId();
        if (!currentVoteHistoryId.containsKey(chatId)) {
            sendMessage(chatId, "Что-то пошло не так, выберите историю повторно и повторите попытку.");
            return;
        }

        var historyId = currentVoteHistoryId.get(chatId);

        var history = historyRepository.findById(historyId);
        if (history.isEmpty()) {
            sendMessage(chatId, "❗ Невозможно проголосовать за эту историю. Возможно она была удалена!");
            return;
        }

        if (history.get().getHistoryStatus() != HistoryStatus.PUBLISH) {
            sendMessage(chatId, "❗ Голосование уже прошло!");
            return;
        }

        var user = userRepository.findByTelegramChatId(chatId);
        if (user.isEmpty()) {
            sendMessage(chatId, "Ваш аккаунт не активирован, чтобы активировать аккаунт - отправьте команду /start");
            return;
        }

        if (checkReplayVote(history.get(), chatId)) {
            sendMessage(chatId, "❗ Вы уже отдали голос за эту историю!");
            return;
        }


        try {
            var newVote = Vote.builder()
                    .user(user.get())
                    .createdAt(LocalDateTime.now())
                    .history(history.get())
                    .amount(Double.parseDouble(message.getText()))
                    .solution(VotingSolution.VOTING_SET_PRICE_YES)
                    .build();

            voteRepository.save(newVote);
            sendMessage(chatId, generateMessage(user.get()));

            currentVoteHistoryId.remove(chatId);
            DataStore.currentCommand.remove(message.getFrom().getId());
        } catch (NumberFormatException e) {
            log.error("error send message to VoteCommand", e);
            sendMessage(chatId, "Отправляйте только числа!");
        }
    }
}
