package ru.eddyz.adminpanel.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.adminpanel.domain.entities.Insurance;
import ru.eddyz.adminpanel.domain.entities.User;
import ru.eddyz.adminpanel.domain.entities.Wallet;
import ru.eddyz.adminpanel.repositories.UserRepository;
import ru.eddyz.adminpanel.repositories.WalletRepository;
import ru.eddyz.adminpanel.services.TasteService;
import util.Render;

import java.util.concurrent.atomic.AtomicReference;


@Route(value = "/users", layout = MainView.class)
@PermitAll
@Slf4j
public class UserView extends HorizontalLayout implements HasUrlParameter<String> {

    private final TextField textField;

    private final VerticalLayout userInfo;
    private final VerticalLayout userHistories;
    private final VerticalLayout userPayments;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TelegramClient telegramClient;
    private final TasteService tasteService;


    public UserView(UserRepository userRepository, WalletRepository walletRepository, TelegramClient telegramClient, TasteService tasteService) {
        this.walletRepository = walletRepository;
        this.telegramClient = telegramClient;
        this.tasteService = tasteService;
        setSizeFull();

        textField = new TextField();
        textField.setVisible(false);
        textField.setReadOnly(true);
        textField.setWidth("100%");

        userInfo = new VerticalLayout();
        userInfo.setWidth("45%");
        userInfo.setVisible(false);
        userInfo.setHeight("1300px");
        userInfo.setClassName(".custom-media-card");

        userHistories = new VerticalLayout();
        userHistories.setVisible(false);
        userHistories.setWidth("100%");
        userHistories.setHeight("650px");
        userHistories.setClassName("custom-media-card");
        userPayments = new VerticalLayout();
        userPayments.setWidth("100%");
        userPayments.setHeight("650px");
        userPayments.setClassName("custom-media-card");

        var paymentHistories = new VerticalLayout(userHistories, userPayments);
        paymentHistories.setWidth("100%");
        paymentHistories.setHeight("1300px");

        add(textField, userInfo, paymentHistories);
        this.userRepository = userRepository;
    }


    @Override
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String s) {
        long userId;
        try {
            userId = Long.parseLong(s);
        } catch (NumberFormatException e) {
            textField.setVisible(true);
            textField.setValue("Пользователь не найден!");
            return;
        }

        var userOp = userRepository.findById(userId);

        if (userOp.isEmpty()) {
            textField.setVisible(true);
            textField.setValue("Пользователь не найден!");
            return;
        }
        var user = userOp.get();

        userInfo.setVisible(true);
        userHistories.setVisible(true);
        userPayments.setVisible(true);

        addUserInfo(user);

        var historiesList = Render.createHistoryList(user.getHistories(), "420px", "450px");
        var hader = new TextField();
        hader.setReadOnly(true);
        hader.setWidth("100%");
        hader.setValue("Истории пользователя");

        userHistories.add(hader, historiesList);

        var payments = Render.cratePaymentList(user.getPayments()
                .stream()
                .sorted(((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt())))
                .toList());

        var paymentsHeader = new TextField();
        paymentsHeader.setWidth("100%");
        paymentsHeader.setReadOnly(true);
        paymentsHeader.setValue("Платежи пользователя");

        userPayments.add(paymentsHeader, payments);


    }

    private void addUserInfo(User user) {
        var username = new TextField("Username");
        username.setReadOnly(true);
        username.setWidth("100%");
        username.setValue(user.getUsername());

        var telegramId = new TextField("Telegram ID");
        telegramId.setReadOnly(true);
        telegramId.setWidth("100%");
        telegramId.setValue(user.getTelegramChatId().toString());

        var createdAt = new DatePicker("Дата регистрации");
        createdAt.setReadOnly(true);
        createdAt.setWidth("100%");
        createdAt.setValue(user.getCreatedAt().toLocalDate());

        AtomicReference<Wallet> wallet = new AtomicReference<>(user.getWallet());
        var walletBlock = new VerticalLayout();
        walletBlock.setWidth("100%");

        var number = new TextField("Привязанный Ton кошелек");
        number.setReadOnly(true);
        number.setWidth("100%");
        number.setValue(wallet.get().getAccountId());
        walletBlock.add(number);

        if (wallet.get() == null) {
            var balanceBlock = new HorizontalLayout();
            balanceBlock.setSizeFull();

            var balance = new NumberField();
            balance.setWidth("30%");
            balance.setValue(wallet.get().getBalance());
            balanceBlock.add(balance);

            var token = new TextField();
            token.setWidth("30%");
            token.setReadOnly(true);
            token.setValue(wallet.get().getToken());
            balanceBlock.add(token);

            var editBalanceButton = createEditBalanceButton(user, wallet, balance);
            balanceBlock.add(editBalanceButton);

            walletBlock.add(balanceBlock);
        }

        var insuranceBlock = new VerticalLayout();
        insuranceBlock.setClassName("custom-media-card");
        insuranceBlock.setHeight("100%");
        var sum = user.getInsurance().stream()
                .mapToDouble(Insurance::getAmount)
                .sum();
        var insurancesHeader = new Span("Купленные страховки. Общая сумма покупок: %s %s".formatted(sum, wallet.get().getToken()));
        insuranceBlock.add(insurancesHeader);
        VirtualList<Insurance> insurances = createInsuranceLisit(user);
        insuranceBlock.add(insurances);
        userInfo.add(username, telegramId, createdAt, walletBlock, insuranceBlock);
    }

    @NotNull
    private VirtualList<Insurance> createInsuranceLisit(User user) {
        VirtualList<Insurance> insurances = new VirtualList<>();
        insurances.setSizeFull();
        insurances.setItems(user.getInsurance());
        insurances.setRenderer(new ComponentRenderer<Component, Insurance>(insurance -> {
            var insuranceCard = new FormLayout();
            insuranceCard.setClassName("custom-media-card");

            var active = new TextField("Активна");
            active.setReadOnly(true);
            active.setWidth("100%");
            active.setValue(insurance.getActive().toString());
            insuranceCard.add(active);

            var priceLayout = new TextField("Цена");
            priceLayout.setReadOnly(true);
            priceLayout.setWidth("100%");
            priceLayout.setValue("%s %s".formatted(insurance.getAmount().toString(), insurance.getCurrency()));
            insuranceCard.add(priceLayout);

            var startData = new DateTimePicker("Дата покупки");
            startData.setReadOnly(true);
            startData.setWidth("100%");
            startData.setValue(insurance.getStartDate());
            insuranceCard.add(startData);

            var endTime = new DateTimePicker("Окончание страховки");
            endTime.setReadOnly(true);
            endTime.setWidth("100%");
            endTime.setValue(insurance.getEndDate());
            insuranceCard.add(endTime);

            insuranceCard.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
            insuranceCard.setColspan(active, 1);
            insuranceCard.setColspan(priceLayout, 1);
            insuranceCard.setColspan(startData, 1);
            insuranceCard.setColspan(endTime, 1);

            return insuranceCard;
        }));
        return insurances;
    }

    @NotNull
    private Button createEditBalanceButton(User user, AtomicReference<Wallet> wallet, NumberField balance) {
        var editBalanceButton = new Button("Изменить баланс");
        editBalanceButton.addClickListener(e -> {
            wallet.get().setBalance(balance.getValue());
            wallet.set(walletRepository.save(wallet.get()));
            tasteService.taste(Notification.Position.TOP_CENTER, NotificationVariant.LUMO_SUCCESS, "Баланс успешно изменен!");
            try {
                telegramClient.execute(SendMessage.builder()
                        .text("Администратор изменил вам баланс на: %s".formatted(balance.getValue()))
                        .chatId(user.getTelegramChatId())
                        .build());
            } catch (TelegramApiException ex) {
                log.error("Ошибка при отправке сообщения", ex);
                tasteService.taste(Notification.Position.TOP_CENTER, NotificationVariant.LUMO_ERROR, "Ошибка при отправке сообщения, но баланс был изменен!");
            }
        });
        return editBalanceButton;
    }
}
