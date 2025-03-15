package ru.eddyz.adminpanel.views;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import jakarta.annotation.security.PermitAll;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.adminpanel.domain.entities.User;
import ru.eddyz.adminpanel.repositories.UserRepository;
import ru.eddyz.adminpanel.repositories.WalletRepository;
import ru.eddyz.adminpanel.services.TasteService;
import util.DateTimeHelper;


@Route(value = "users", layout = MainView.class)
@PageTitle("Пользователи")
@PermitAll
public class UsersView extends VerticalLayout {


    private final TelegramClient telegramClient;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TasteService tasteService;
    private GridListDataView<User> dataView;
    private final TextField search;

    public UsersView(TelegramClient telegramClient, UserRepository userRepository, WalletRepository walletRepository, TasteService tasteService) {
        this.telegramClient = telegramClient;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.tasteService = tasteService;

        setSizeFull();

        var users = configUsers();

        search = new TextField("Поиск по username");
        search.setPlaceholder("Поиск...");
        search.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        search.setValueChangeMode(ValueChangeMode.EAGER);
        search.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(user -> {
            String searchTerm = search.getValue().trim();
            if (searchTerm.isEmpty())
                return true;

            return user.getUsername().toLowerCase().contains(searchTerm);
        });

        add(search, users);
    }


    public Grid<User> configUsers() {
        final Grid<User> users;

        users = new Grid<>(User.class, false);
        users.addColumn(User::getId).setHeader("ID").setAutoWidth(true);
        users.addColumn(User::getUsername).setHeader("Username").setAutoWidth(true);
        users.addColumn(user -> user.getHistories().size())
                .setHeader("Добавлено историй")
                .setAutoWidth(true);
        users.addColumn(user -> user.getWithdraws().size())
                .setHeader("Снятий")
                .setAutoWidth(true);
        users.addColumn(user -> user.getPayments().size())
                .setAutoWidth(true)
                .setHeader("Платежей");
        users.addColumn(user -> user.getVotes().size())
                .setHeader("Отдано голосов")
                .setAutoWidth(true);
        users.addColumn(user -> user.getCreatedAt().format(DateTimeHelper.defaultDateTimeFormatter))
                .setHeader("Дата регистрации")
                .setAutoWidth(true);

        users.addComponentColumn(this::balanceComponent)
                .setHeader("Баланс")
                .setAutoWidth(true);

        users.addComponentColumn(user -> new RouterLink("Открыть пользователя", UserView.class, user.getId().toString()))
                .setAutoWidth(true);

        dataView = users.setItems(userRepository.findAll());
        return users;
    }

    @NotNull
    private Component balanceComponent(User user) {
        if (user.getWallet() == null) {
            return new Span("Кошелек не привязан");
        }
        var wallet = user.getWallet();
        var hor = new HorizontalLayout();
        var balance = new NumberField();
        balance.setWidth("50%");
        balance.setValue(wallet.getBalance());
        var iconButton = VaadinIcon.CHECK_SQUARE.create();

        iconButton.addClickListener(e -> {
            wallet.setBalance(balance.getValue());
            walletRepository.save(wallet);
            tasteService.taste(Notification.Position.TOP_CENTER, NotificationVariant.LUMO_SUCCESS, "Баланс изменен");
            try {
                telegramClient.execute(SendMessage.builder()
                        .chatId(user.getTelegramChatId())
                        .text("Администратор изменил ваш баланс на %s".formatted(balance.getValue()))
                        .build());
            } catch (TelegramApiException ex) {
                tasteService.taste(Notification.Position.TOP_CENTER, NotificationVariant.LUMO_ERROR, "При попытке отправить сообщение пользователю произошла ошибка, но баланс изменен!");
            }
        });

        hor.add(balance, iconButton);
        return hor;
    }

    private void taste() {
        Notification notification = Notification.show("Кол-во символов в группе изменено", 3000,
                Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

}

