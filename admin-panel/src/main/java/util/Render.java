package util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.RouterLink;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.adminpanel.components.Video;
import ru.eddyz.adminpanel.domain.entities.History;
import ru.eddyz.adminpanel.domain.entities.Payment;
import ru.eddyz.adminpanel.domain.entities.Withdraw;
import ru.eddyz.adminpanel.domain.enums.WithdrawStatus;
import ru.eddyz.adminpanel.repositories.WalletRepository;
import ru.eddyz.adminpanel.repositories.WithdrawRepository;
import ru.eddyz.adminpanel.views.HistroryView;
import ru.eddyz.adminpanel.views.UserView;

import java.util.List;
import java.util.Objects;


@Slf4j
public class Render {

    public static VirtualList<History> createHistoryList(List<History> histories, String width, String height) {
        var historiesList = new VirtualList<History>();
        historiesList.setItems(histories
                .stream()
                .sorted(((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))));
        historiesList.setRenderer(new ComponentRenderer<Component, History>(history -> {
            var historyBlock = new VerticalLayout();
            historyBlock.setClassName("custom-media-card");
            historyBlock.setWidth("90%");

            RouterLink historyLonk = new RouterLink("Перейти в настройки истории", HistroryView.class, history.getId().toString());
            historyBlock.add(historyLonk);

            RouterLink userLink = new RouterLink("%s".formatted(history.getUser().getUsername()), UserView.class, history.getUser().getId().toString());
            historyBlock.add(userLink);

            var status = new TextField("Статус");
            status.setWidth("100%");
            status.setValue(history.getHistoryStatus().toString());
            status.setReadOnly(true);
            historyBlock.add(status);

            var amountAndCurrency = new TextField("Желаемая сумма");
            amountAndCurrency.setWidth("100%");
            amountAndCurrency.setReadOnly(true);
            amountAndCurrency.setValue("%s %s".formatted(history.getAmount(), history.getCurrency()));
            historyBlock.add(amountAndCurrency);

            if (history.getFiles() != null && !history.getFiles().isEmpty()) {
                history.getFiles()
                        .stream()
                        .findFirst()
                        .ifPresent(f -> {
                            if (f.getFilePaths() != null) {
                                var fl = f.getFilePaths().getFirst();
                                var streamResource = ResourseHelper.getStreamResource("История", fl.getPath());

                                switch (f.getFileType()) {
                                    case PHOTO -> {
                                        Image image = new Image(streamResource, "Фото");
                                        image.setMaxHeight(height);
                                        image.setMaxWidth(width);
                                        image.setHeight("auto");
                                        image.setWidth("auto");
                                        historyBlock.add(image);
                                    }
                                    case VIDEO -> {
                                        Video video = new Video(streamResource, "Видео");
                                        video.setHeight(height);
                                        video.setWidth(width);
                                        video.setMaxHeight(height);
                                        video.setMaxWidth(width);
                                        historyBlock.add(video);
                                    }
                                }

                            }
                        });
            }

            var desc = new TextArea();
            desc.setReadOnly(true);
            desc.setWidth("100%");
            desc.setHeight("150px");
            desc.setValue(history.getDescription());
            historyBlock.add(desc);

            return historyBlock;
        }));
        return historiesList;
    }

    public static VirtualList<Payment> cratePaymentList(List<Payment> payment) {
        VirtualList<Payment> paymentList = new VirtualList<>();

        paymentList.setItems(payment);
        paymentList.setRenderer(new ComponentRenderer<Component, Payment>(p -> {
            var paymentBlock = new VerticalLayout();
            paymentBlock.setClassName("custom-media-card");
            paymentBlock.setWidth("95%");

            RouterLink userLink = new RouterLink(p.getUser().getUsername(), UserView.class, p.getUser().getId().toString());
            paymentBlock.add(userLink);

            var hash = new TextField("Hash платежа");
            hash.setReadOnly(true);
            hash.setWidth("100%");
            hash.setValue(p.getHash());
            paymentBlock.add(hash);

            var amountAndCreatedAt = new HorizontalLayout();
            amountAndCreatedAt.setWidth("100%");

            var amount = new TextField("Сумма платежа");
            amount.setWidth("100%");
            amount.setReadOnly(true);
            amount.setValue("%s %s".formatted(p.getAmount(), p.getCurrency()));
            amountAndCreatedAt.add(amount);

            var createdAt = new DateTimePicker("Дата");
            createdAt.setReadOnly(true);
            createdAt.setWidth("40%");
            createdAt.setValue(p.getCreatedAt());
            amountAndCreatedAt.add(createdAt);

            paymentBlock.add(amountAndCreatedAt);

            return paymentBlock;
        }));
        return paymentList;
    }

    public static ComponentRenderer<Component, Withdraw> withdrawRender(TelegramClient telegramClient, WithdrawRepository repo, WalletRepository walletRepository) {
        return new ComponentRenderer<>(withdraw -> {
            var withdawBlock = new VerticalLayout();
            withdawBlock.setClassName("custom-media-card");
            withdawBlock.setWidth("95%");

            var userLink =  new RouterLink(withdraw.getUser().getUsername(), UserView.class, withdraw.getUser().getId().toString());
            withdawBlock.add(userLink);

            var currentAmount =  withdraw.getAmount();

            var amount = new NumberField("Желаемая сумма выплаты в %s".formatted(withdraw.getToken()));
            amount.setValue(currentAmount);
            amount.setWidthFull();


            var status = new TextField("Статус");
            status.setReadOnly(true);
            status.setValue("%s".formatted(withdraw.getStatus()));
            status.setWidthFull();

            var statusAndAmount = new HorizontalLayout(status, amount);
            statusAndAmount.setWidthFull();

            withdawBlock.add(statusAndAmount);

            if (withdraw.getActive().equals(Boolean.TRUE)) {
                var message = new TextArea("Сообщение для пользователя");
                message.setWidthFull();
                message.setHeight("170px");
                message.setPlaceholder("Тут вы можете ввести дополнительное сообщение пользователю. Например причину отказа, или изменение суммы выплаты....");
                withdawBlock.add(message);

                var decline = new Button("Отказать");
                var approved = new Button("Одобрить");
                approved.addClickListener(e -> {
                    withdraw.setAmount(amount.getValue());
                    withdraw.setStatus(WithdrawStatus.APPROVED);
                    withdraw.setActive(false);
                    repo.save(withdraw);

                    var messageTelegram = "❗ Администратор одобрил заявку на вывод с запрашиваемой суммой!\n\n";

                    if (!Objects.equals(currentAmount, amount.getValue())) {
                        if (currentAmount -  amount.getValue() > 0) {
                            messageTelegram = "❗ Администратор одобрил заявку на вывод, но сумма была изменена на %s. Разница возвращена на баланс!\n\n".formatted(amount.getValue());
                            var result = currentAmount - amount.getValue();
                            var wallet = withdraw.getWallet();
                            wallet.setBalance(wallet.getBalance() + result);
                            walletRepository.save(wallet);
                        } else {
                            messageTelegram = "❗ Администратор одобрил заявку на вывод, но сумма была изменена на %s.\n\n".formatted(amount.getValue());
                        }
                    }

                    if (!message.getValue().isEmpty()) {
                        messageTelegram += message.getValue();
                    }

                    sendMessage(withdraw, telegramClient, messageTelegram);
                    message.setVisible(false);
                    decline.setVisible(false);
                    approved.setVisible(false);
                });

                decline.addClickListener(e -> {
                    withdraw.setAmount(amount.getValue());
                    withdraw.setStatus(WithdrawStatus.DECLINE);
                    withdraw.setActive(false);
                    repo.save(withdraw);

                    var messageTelegram = "❗ Администратор отклонил заявку на вывод! Средства возвращены вам на баланс!\n\n";

                    if (!message.getValue().isEmpty()) {
                        messageTelegram += message.getValue();
                    }

                    sendMessage(withdraw, telegramClient, messageTelegram);
                    message.setVisible(false);
                    decline.setVisible(false);
                    approved.setVisible(false);
                    var wallet = withdraw.getWallet();
                    wallet.setBalance(wallet.getBalance() + currentAmount);
                    walletRepository.save(wallet);
                });

                var buttonsRow = new HorizontalLayout(approved, decline);
                withdawBlock.add(buttonsRow);
            }
            return withdawBlock;
        });
    }

    private static void sendMessage(Withdraw withdraw, TelegramClient telegramClient, String messageTelegram) {
        try {
            telegramClient.execute(SendMessage.builder()
                    .chatId(withdraw.getUser().getTelegramChatId())
                    .text(messageTelegram)
                    .build());
        } catch (TelegramApiException ex) {
            log.error("Ошибка при отправке сообщения!", ex);
        }
    }
}
