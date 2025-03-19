package ru.eddyz.telegrambot.commands.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.ShowResultVoteCommand;
import ru.eddyz.telegrambot.domain.entities.History;
import ru.eddyz.telegrambot.domain.entities.Vote;
import ru.eddyz.telegrambot.domain.enums.VotingSolution;
import ru.eddyz.telegrambot.repositories.HistoryRepository;
import ru.eddyz.telegrambot.util.Sender;


@Slf4j
@Component
@RequiredArgsConstructor
public class ShowResultVoteCommandImpl implements ShowResultVoteCommand {

    private final HistoryRepository historyRepository;
    private final TelegramClient telegramClient;


    @Override
    @Transactional
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getFrom().getId();
        var dataSplit = callbackQuery.getData().split(":");
        try {
            var message = ((Message) callbackQuery.getMessage());
            var historyId = Long.parseLong(dataSplit[1]);
            var text = message.getText() == null ? message.getCaption() : message.getText();

            var history = historyRepository.findById(historyId);
            if (history.isEmpty()) {
                sendMessage(chatId, "❗ Невозможно посмотреть результаты. Возможно, история была удалена.");
                return;
            }

            try {
                telegramClient.execute(EditMessageText.builder()
                        .text(generateMessage(text, history.get()))
                        .messageId(callbackQuery.getMessage().getMessageId())
                        .parseMode(ParseMode.HTML)
                        .chatId(chatId)
                        .build());
            } catch (TelegramApiException e) {
                log.error("Error edit message to ShowResultVoteCommand", e);
            }

        } catch (Exception e) {
            log.error("error cast message to showResultVoteCommand::execute", e);
            sendMessage(chatId, "Что-то пошло не так, попробуйте повторить попытку!");
        }
    }

    private void sendMessage(Long chatId, String message) {
        try {
            telegramClient.execute(Sender.sendMessage(chatId, message));
        } catch (TelegramApiException e) {
            log.error("error sending showResultVoteCommand::execute", e);
        }
    }

    private String generateMessage(String oldMessage, History history) {
        var midAmount = history.getVotes().stream()
                .filter(vote -> vote.getSolution() == VotingSolution.VOTING_YES ||
                                vote.getSolution() == VotingSolution.VOTING_SET_PRICE_YES)
                .mapToDouble(Vote::getAmount)
                .average()
                .orElse(0);


        var yesCount = history.getVotes()
                .stream()
                .filter(vote -> vote.getSolution() == VotingSolution.VOTING_YES ||
                                vote.getSolution() == VotingSolution.VOTING_SET_PRICE_YES)
                .toList()
                .size();

        var noCount = history.getVotes()
                .stream()
                .filter(vote -> vote.getSolution() == VotingSolution.VOTING_NO)
                .toList()
                .size();

        return """
                %s
                
                <b>Результаты: </b>
                
                <b>• Средняя сумма после голосования:</b> %s %s
                <b>• Голосов за:</b> %d
                <b>• Голосов против:</b> %d"""
                .

                formatted(
                        oldMessage,
                        midAmount, history.getCurrency(),
                        yesCount,
                        noCount
                );
    }
}
