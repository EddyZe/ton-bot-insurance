package ru.eddyz.adminpanel.views;


import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import ru.eddyz.adminpanel.repositories.PaymentRepository;
import util.Render;

@Route(value = "/payments", layout = MainView.class)
@PermitAll
@Slf4j
@PageTitle("Платежи")
public class PaymentsView extends VerticalLayout {

    public PaymentsView(PaymentRepository paymentRepository) {

        setSizeFull();
        var payments = Render.cratePaymentList(paymentRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")));
        payments.setWidthFull();

        add(payments);

    }
}
