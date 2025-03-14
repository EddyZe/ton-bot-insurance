package ru.eddyz.adminpanel.views;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.adminpanel.domain.entities.History;
import ru.eddyz.adminpanel.domain.entities.Vote;
import ru.eddyz.adminpanel.domain.enums.HistoryStatus;
import ru.eddyz.adminpanel.domain.enums.VotingSolution;
import ru.eddyz.adminpanel.repositories.HistoryRepository;
import ru.eddyz.adminpanel.repositories.WalletRepository;
import ru.eddyz.adminpanel.services.TasteService;
import util.Render;


@Route(value = "approvedHistories", layout = MainView.class)
@PermitAll
@Slf4j
@PageTitle("Истории, которые ждут окончательного решения")
public class AwaitApprovedHistoryView extends VerticalLayout {


    private final HistoryRepository historyRepository;
    private final WalletRepository walletRepository;

    private final TelegramClient telegramClient;
    private final TasteService tasteService;

    private final VirtualList<History> histories;

    public AwaitApprovedHistoryView(HistoryRepository historyRepository1, WalletRepository walletRepository1, TelegramClient telegramClient, TasteService tasteService) {
        this.historyRepository = historyRepository1;
        this.walletRepository = walletRepository1;
        this.telegramClient = telegramClient;
        this.tasteService = tasteService;

        setClassName("custom-media-card");
        setWidth("95%");
        setHeight("95%");

        histories = new VirtualList<>();
        refreshHistories();
        histories.setRenderer(new ComponentRenderer<Component, History>(history -> {
            var defRender = Render.createRenderHistory("950px", "680px", history);

            var resultVotes = new TextArea("Результаты голосования");
            resultVotes.setWidth("100%");
            resultVotes.setReadOnly(true);
            resultVotes.setValue(generateResultVote(history));
            defRender.add(resultVotes);

            var message = new TextArea();
            message.setWidth("100%");
            message.setPlaceholder("Тут вы можете ввести дополнительное сообщение.... Например причину отказа....");
            defRender.add(message);


            var buttons = new HorizontalLayout();
            var approved = createApprovedButton(history, message);
            buttons.add(approved);

            var declineButton = createDeclineButton(history, message);

            buttons.add(declineButton);

            defRender.add(buttons);
            return defRender;
        }));

        add(histories);
    }

    @NotNull
    private Button createApprovedButton(History history, TextArea messageField) {
        var approved = new Button("Согласиться с решением");
        approved.addClickListener(event -> {
            history.setHistoryStatus(HistoryStatus.APPROVED);
            var wallet = history.getUser().getWallet();
            wallet.setBalance(wallet.getBalance() + history.getAmount());
            historyRepository.save(history);
            walletRepository.save(wallet);
            sendMessageTelegram(wallet.getUser().getTelegramChatId(),  "🎉 Ваша история одобрена! Ваш баланс пополнен на сумму %s %s\n\n%s"
                    .formatted(history.getAmount(), history.getCurrency(), messageField.getValue()));
            tasteService.taste(Notification.Position.TOP_CENTER, NotificationVariant.LUMO_SUCCESS, "История одобрена!");
            refreshHistories();
        });
        return approved;
    }

    @NotNull
    private Button createDeclineButton(History history, TextArea messageField) {
        var declineButton = new Button("Отказать в выплате");
        declineButton.addClickListener(event -> {
            history.setHistoryStatus(HistoryStatus.DECLINE);
            historyRepository.save(history);
            sendMessageTelegram(history.getUser().getTelegramChatId(), "❗ Вам отказали в выплате!\n\n%s"
                    .formatted(messageField.getValue()));
            tasteService.taste(Notification.Position.TOP_CENTER, NotificationVariant.LUMO_SUCCESS, "В выплате отказано!");
            refreshHistories();
        });
        return declineButton;
    }

    private void refreshHistories() {
        histories.setItems(historyRepository.findByHistoryStatus(HistoryStatus.AWAITING_APPROVED, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    private void sendMessageTelegram(Long chatId, String message) {
        try {
            telegramClient.execute(SendMessage.builder()
                    .text(message)
                    .parseMode(ParseMode.HTML)
                    .chatId(chatId)
                    .build());
        } catch (TelegramApiException ex) {
            log.error(ex.getMessage(), ex);
            tasteService.taste(Notification.Position.TOP_CENTER, NotificationVariant.LUMO_ERROR, "Произошла ошибка при отправке сообщения в телеграм, но статус был изменен.");
        }
    }

    private String generateResultVote(History history) {
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
                • Средняя сумма после голосования: %s %s
                • Голосов за: %d
                • Голосов против: %d"""
                .

                formatted(
                        midAmount, history.getCurrency(),
                        yesCount,
                        noCount
                );
    }
}
