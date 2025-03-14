package ru.eddyz.adminpanel.views;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import jakarta.annotation.security.PermitAll;
import org.springframework.transaction.annotation.Transactional;
import ru.eddyz.adminpanel.domain.entities.History;
import ru.eddyz.adminpanel.domain.enums.HistoryStatus;
import ru.eddyz.adminpanel.repositories.HistoryRepository;

import java.util.List;

@Route(value = "/awaitPublishing", layout = MainView.class)
@PageTitle("Историю ждут публикации")
@PermitAll
@Transactional(readOnly = true)
public class AwaitPublishingList extends VerticalLayout {

    private final HistoryRepository historyRepository;

    private final VirtualList<History> histories;


    public AwaitPublishingList(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
        List<History> awaitPublisherHistory = this.historyRepository.findByHistoryStatus(HistoryStatus.ADMIN_CHECKING);
        setSizeFull();


        histories = new VirtualList<>();
        histories.setWidth("100%");
        histories.setItems(awaitPublisherHistory);
        histories.setRenderer(new ComponentRenderer<Component, History>(history -> {
            var ver = new VerticalLayout();
            ver.setWidth("80%");
            var hor = new VerticalLayout();
            hor.setWidth("100%");
            hor.setHorizontalComponentAlignment(Alignment.END);
            ver.setClassName("custom-media-card");
            setHeightFull();

            TextArea desc = new TextArea();
            desc.setReadOnly(true);
            desc.setWidth("100%");
            desc.setValue(generateDescriptionHistory(history));


            RouterLink historyLink = new RouterLink("Перейти к истории", HistroryView.class, history.getId().toString());
            RouterLink userLink = new RouterLink("История от пользователя %s".formatted(history.getUser().getUsername()), UserView.class, history.getUser().getId().toString());

            hor.add(desc, historyLink);
            ver.add(userLink, hor);

            return ver;
        }));

        add(histories);
    }

    private String generateDescriptionHistory(History history) {
        String desc = history.getDescription();

        if (desc.length() > 500) {
            desc = desc.substring(0, 500) + "...";
        }

        return """
                %s""".formatted(desc);
    }
}
