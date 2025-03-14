package ru.eddyz.adminpanel.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.adminpanel.domain.entities.Withdraw;
import ru.eddyz.adminpanel.domain.enums.WithdrawStatus;
import ru.eddyz.adminpanel.repositories.WalletRepository;
import ru.eddyz.adminpanel.repositories.WithdrawRepository;
import util.Render;


@Route(value = "/withdraws", layout = MainView.class)
@PermitAll
@Slf4j
@PageTitle("Запросы на вывод")
public class WithdrawsView extends VerticalLayout {

    private final VirtualList<Withdraw> withdraws;


    public WithdrawsView(WithdrawRepository withdrawRepository, TelegramClient telegramClient, WalletRepository walletRepository) {
        setSizeFull();

        withdraws = new VirtualList<>();
        withdraws.setItems(withdrawRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")));
        withdraws.setRenderer(Render.withdrawRender(telegramClient,  withdrawRepository, walletRepository));

        HorizontalLayout navigationBar = new HorizontalLayout();
        navigationBar.setWidthFull();

        var allButton = new Button("Показать все");
        allButton.addClickListener(e ->
                withdraws.setItems(withdrawRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))));

        var awaitButton = new Button("Ожидают обработки");
        awaitButton.addClickListener(e ->
                withdraws.setItems(
                        withdrawRepository.findByStatus(WithdrawStatus.AWAITING, Sort.by(Sort.Direction.DESC, "createdAt"))
                ));

        var approvedButton = new Button("Подтвержденные");
        approvedButton.addClickListener(e ->
                withdraws.setItems(
                        withdrawRepository.findByStatus(WithdrawStatus.APPROVED, Sort.by(Sort.Direction.DESC, "createdAt"))
                ));

        var declineButton = new Button("Отмененные");
        declineButton.addClickListener(e ->
                withdraws.setItems(
                        withdrawRepository.findByStatus(WithdrawStatus.DECLINE, Sort.by(Sort.Direction.DESC, "createdAt"))
                ));

        navigationBar.add(allButton, awaitButton, approvedButton, declineButton);


        add(navigationBar, withdraws);


    }
}
