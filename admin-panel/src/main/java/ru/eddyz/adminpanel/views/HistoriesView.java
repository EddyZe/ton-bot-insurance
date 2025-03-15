package ru.eddyz.adminpanel.views;


import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.eddyz.adminpanel.repositories.HistoryRepository;
import util.Render;

@Route(value = "/histories", layout = MainView.class)
@PermitAll
@Slf4j
@PageTitle("Истории")
@Transactional
public class HistoriesView extends VerticalLayout {

    private final HistoryRepository historyRepository;

    public HistoriesView(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;

        setClassName("custom-media-card");
        setWidth("95%");
        setHeight("95%");

        var historyList = Render.createHistoryList(this.historyRepository.findAll(), "950px", "680px");

        add(historyList);

    }
}
