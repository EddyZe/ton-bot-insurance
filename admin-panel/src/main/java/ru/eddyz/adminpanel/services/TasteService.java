package ru.eddyz.adminpanel.services;


import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.springframework.stereotype.Service;

@Service
public class TasteService {


    public void taste(Notification.Position position, NotificationVariant variant, String message) {
        Notification notification = Notification.show(message, 3000,
                position);
        notification.addThemeVariants(variant);
    }
}
