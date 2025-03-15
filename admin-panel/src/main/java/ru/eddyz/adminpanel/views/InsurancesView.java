package ru.eddyz.adminpanel.views;


import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import ru.eddyz.adminpanel.domain.entities.Insurance;
import ru.eddyz.adminpanel.repositories.InsuranceRepository;
import util.Render;

@Route(value = "insurances", layout = MainView.class)
@PermitAll
@Slf4j
@PageTitle("История купленных страховок")
public class InsurancesView extends VerticalLayout {


    private final InsuranceRepository insuranceRepository;
    private final VirtualList<Insurance>  insurances;


    public InsurancesView(InsuranceRepository insuranceRepository1) {
        this.insuranceRepository = insuranceRepository1;
        setSizeFull();


        insurances = new VirtualList<>();
        insurances.setSizeFull();
        refreshList();
        insurances.setRenderer(new ComponentRenderer<>(insurance -> {
            var insuranceBlock = new VerticalLayout();
            insuranceBlock.setWidth("90%");
            insuranceBlock.setClassName("custom-media-card");

            var userLing = new RouterLink(insurance.getUser().getUsername(), UserView.class,
                    insurance.getUser().getId().toString());
            insuranceBlock.add(userLing);

            var def = Render.createInsuranceRender(insurance);

            insuranceBlock.add(def);
            return insuranceBlock;
        }));

        add(insurances);
    }

    private void refreshList() {
        insurances.setItems(this.insuranceRepository.findAll(Sort.by(Sort.Direction.DESC, "startDate")));
    }
}
