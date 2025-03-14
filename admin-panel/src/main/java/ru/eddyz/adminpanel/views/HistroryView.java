package ru.eddyz.adminpanel.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.adminpanel.components.Video;
import ru.eddyz.adminpanel.domain.entities.History;
import ru.eddyz.adminpanel.domain.entities.HistoryFile;
import ru.eddyz.adminpanel.domain.entities.HistoryFilePath;
import ru.eddyz.adminpanel.domain.enums.HistoryFileType;
import ru.eddyz.adminpanel.domain.enums.HistoryStatus;
import ru.eddyz.adminpanel.domain.payloads.PublisherRequest;
import ru.eddyz.adminpanel.domain.payloads.Response;
import ru.eddyz.adminpanel.repositories.HistoryFilePathsRepository;
import ru.eddyz.adminpanel.repositories.HistoryFilesRepository;
import ru.eddyz.adminpanel.repositories.HistoryRepository;
import ru.eddyz.adminpanel.services.TasteService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


@Route(value = "/histories", layout = MainView.class)
@PermitAll
@Slf4j
@PageTitle("История пользователя")
@Transactional
public class HistroryView extends VerticalLayout implements HasUrlParameter<String> {

    private final static String FILE_DIR = "/files/%s";

    private final HistoryRepository historyRepository;
    private final EntityManager entityManager;

    @Value("${telegram.bot_token}")
    private String botToken;
    private final HistoryFilePathsRepository historyFilePathsRepository;
    private final HistoryFilesRepository historyFilesRepository;
    private final TasteService tasteService;
    private final TelegramClient telegramClient;

    private final RestClient restClient;

    private final HorizontalLayout main;

    private final Button approve;
    private final Button reject;
    private final Button downloadFiles;

    private final TextArea description;
    private final DateTimePicker createdAt;
    private final NumberField amount;
    private final TextField currency;
    private final TextField username;
    private final TextField status;
    private final VirtualList<HistoryFile> media;

    private final TextField textField;

    private final RouterLink authorLink;


    public HistroryView(HistoryRepository historyRepository, EntityManager entityManager, HistoryFilePathsRepository historyFilePathsRepository, HistoryFilesRepository historyFilesRepository, TasteService tasteService, TelegramClient telegramClient, RestClient restClient) {
        this.historyRepository = historyRepository;
        this.entityManager = entityManager;
        this.historyFilePathsRepository = historyFilePathsRepository;
        this.historyFilesRepository = historyFilesRepository;
        this.tasteService = tasteService;
        this.telegramClient = telegramClient;
        this.restClient = restClient;

        setSizeFull();

        main = new HorizontalLayout();
        main.setWidth("90%");
        main.setVisible(false);

        var info = new FormLayout();
        info.setWidth("100%");

        textField = new TextField();
        textField.setReadOnly(true);
        textField.setVisible(false);
        textField.setWidth("100%");


        authorLink = new RouterLink();
        authorLink.setVisible(false);

        description = new TextArea();
        var priceLayout = new HorizontalLayout();
        priceLayout.setWidth("100%");
        priceLayout.setSpacing(true);
        amount = new NumberField("Желаемая сумма");
        amount.setWidthFull();
        currency = new TextField("Токен");
        currency.setReadOnly(true);
        currency.setWidth("100%");
        priceLayout.add(amount, currency);
        username = new TextField("Пользователь");
        username.setReadOnly(true);
        username.setWidth("100%");
        status = new TextField("Статус объявления");
        status.setReadOnly(true);
        status.setWidth("100%");
        createdAt = new DateTimePicker("Создано");
        createdAt.setReadOnly(true);
        downloadFiles = new Button("Загрузить файлы для просмотра");
        var approveOrReject = new HorizontalLayout();
        approveOrReject.setWidth("100%");
        approve = new Button("Опубликовать");
        approve.setWidthFull();

        reject = new Button("Отказать");
        reject.setWidthFull();
        approveOrReject.add(approve, reject);

        info.add(description, priceLayout, username, status, createdAt, downloadFiles, approveOrReject);
        info.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        info.setColspan(description, 2);
        info.setColspan(priceLayout, 2);
        info.setColspan(username, 1);
        info.setColspan(status, 1);
        info.setColspan(createdAt, 2);
        info.setColspan(downloadFiles, 2);
        info.setColspan(approve, 1);
        info.setColspan(reject, 1);

        var mediaLayout = new VerticalLayout();
        media = new VirtualList<>();
        media.setVisible(false);
        media.setSizeFull();
        mediaLayout.add(media);

        main.add(info, mediaLayout);

        add(textField, authorLink, main);
    }


    @Override
    @Transactional
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String s) {
        long historyId;
        try {
            historyId = Long.parseLong(s);
        } catch (NumberFormatException e) {
            textField.setVisible(true);
            textField.setValue("Такая история не существует!");
            return;
        }

        var historyOp = historyRepository.findById(historyId);

        if (historyOp.isEmpty()) {
            textField.setVisible(true);
            textField.setValue("Такая история не существует!");
            return;
        }

        main.setVisible(true);
        authorLink.setVisible(true);


        AtomicReference<History> history = new AtomicReference<>(historyOp.get());
        authorLink.setRoute(UserView.class, history.get().getUser().getId().toString());
        authorLink.setText("Перейти на страницу автора");

        description.setValue(history.get().getDescription());
        amount.setValue(history.get().getAmount());
        currency.setValue(history.get().getCurrency());
        status.setValue(history.get().getHistoryStatus().toString());
        createdAt.setValue(history.get().getCreatedAt());
        username.setValue(history.get().getUser().getUsername());

        reject.addClickListener(event -> {
            if (checkHistory(history.get())) return;
            history.set(historyRepository.findById(historyId)
                    .orElseThrow());
            history.get().setUpdatedAt(LocalDateTime.now());
            history.get().setAmount(amount.getValue());
            history.get().setDescription(description.getValue());
            history.get().setHistoryStatus(HistoryStatus.DECLINE);
            historyRepository.saveAndFlush(history.get());

            var resp = publishRequest(history.get());

            if (resp == null) {
                return;
            }

            if (!resp.getStatus().contains(HttpStatus.OK.toString())) {
                tasteService.taste(Notification.Position.TOP_CENTER, NotificationVariant.LUMO_ERROR, resp.getMessage());
                return;
            }

            tasteService.taste(Notification.Position.TOP_CENTER, NotificationVariant.LUMO_SUCCESS, "Публикация отклонена!");
        });

        approve.addClickListener(e -> {
            if (checkHistory(history.get())) return;

            history.set(historyRepository.findById(historyId)
                    .orElseThrow());
            history.get().setUpdatedAt(LocalDateTime.now());
            history.get().setAmount(amount.getValue());
            history.get().setDescription(description.getValue());
            history.get().setHistoryStatus(HistoryStatus.PUBLISH);

            historyRepository.saveAndFlush(history.get());
            var resp = publishRequest(history.get());

            if (resp == null) {
                return;
            }

            if (!resp.getStatus().equals(HttpStatus.OK.toString())) {
                tasteService.taste(Notification.Position.TOP_CENTER, NotificationVariant.LUMO_ERROR, resp.getMessage());
                return;
            }
            tasteService.taste(Notification.Position.TOP_CENTER, NotificationVariant.LUMO_SUCCESS, "Публикация одобрена!");
        });

        downloadFiles.addClickListener(e -> {
            if (history.get().getFiles().isEmpty()) {
                tasteService.taste(Notification.Position.TOP_CENTER, NotificationVariant.LUMO_CONTRAST, "У этой истории нет файлов");
                return;
            }

            media.setVisible(true);
            media.setItems(history.get().getFiles());
            media.setRenderer(historyFileRenderer(historyId));
        });
    }

    public Renderer<HistoryFile> historyFileRenderer(Long historyId) {
        return new ComponentRenderer<Component, HistoryFile>(historyFile -> {
            var type = historyFile.getFileType();

            var dir = Path.of(System.getProperty("user.dir"), FILE_DIR.formatted(historyId));

            try {
                if (!Files.exists(dir)) {
                    Files.createDirectories(dir);
                    log.info("Successfully created directory: {}", dir);
                } else {
                    log.info("Directory already exists: {} ", dir);
                }
            } catch (IOException e) {
                log.error("Failed to create directory:", e);
            }

            switch (type) {
                case HistoryFileType.PHOTO -> {
                    try {
                        var photoUrl = telegramClient.execute(GetFile.builder()
                                .fileId(historyFile.getTelegramFileId())
                                .build());


                        Path resourcesDir = getResourcesDir(historyFile);

                        if (!Files.exists(resourcesDir)) {
                            Files.createDirectories(resourcesDir);
                        }

                        String fileName = seNameMediaFile(historyFile, photoUrl);


                        Path filePath = resourcesDir.resolve(fileName);


                        if (filePath.toFile().exists()) {
                            StreamResource streamResource = getStreamResource(fileName, filePath);

                            return createMediaCard(historyFile, filePath, getImage(streamResource), historyId);
                        }

                        try (InputStream is = new URL(photoUrl.getFileUrl(botToken)).openStream()) {
                            Files.copy(is, filePath, StandardCopyOption.REPLACE_EXISTING);
                        }

                        StreamResource streamResource = getStreamResource(fileName, filePath);
                        saveFilePath(historyFile, filePath);

                        return createMediaCard(historyFile, filePath, getImage(streamResource), historyId);

                    } catch (Exception e) {
                        log.error("Ошибка загрузки файла", e);
                        return new Image();
                    }
                }
                case HistoryFileType.VIDEO -> {
                    try {
                        var photoUrl = telegramClient.execute(GetFile.builder()
                                .fileId(historyFile.getTelegramFileId())
                                .build());

                        Path resourcesDir = getResourcesDir(historyFile);

                        if (!Files.exists(resourcesDir)) {
                            Files.createDirectories(resourcesDir);
                        }

                        String fileName = seNameMediaFile(historyFile, photoUrl);


                        Path filePath = resourcesDir.resolve(fileName);

                        if (filePath.toFile().exists()) {
                            StreamResource streamResource = getStreamResource(fileName, filePath);
                            Video video = getVideo(streamResource);
                            return createMediaCard(historyFile, filePath, video, historyId);
                        }

                        try (InputStream is = new URL(photoUrl.getFileUrl(botToken)).openStream()) {
                            Files.copy(is, filePath, StandardCopyOption.REPLACE_EXISTING);
                        }

                        StreamResource streamResource = getStreamResource(fileName, filePath);
                        saveFilePath(historyFile, filePath);

                        Video video = getVideo(streamResource);
                        return createMediaCard(historyFile, filePath, video, historyId);

                    } catch (Exception e) {
                        log.error("Ошибка загрузки файла", e);
                        return new Image();
                    }
                }
            }

            return new FormLayout();
        });
    }


    private void saveFilePath(HistoryFile historyFile, Path filePath) {
        historyFilePathsRepository.save(HistoryFilePath.builder()
                .path(filePath.toString())
                .file(historyFile)
                .build());
    }

    @NotNull
    private String seNameMediaFile(HistoryFile historyFile, File photoUrl) {
        return historyFile.getTelegramFileId() +
               photoUrl.getFilePath()
                       .substring(photoUrl.getFilePath().lastIndexOf("."));
    }

    @NotNull
    private Path getResourcesDir(HistoryFile historyFile) {
        return Path.of(
                System.getProperty("user.dir"),
                FILE_DIR.formatted(historyFile.getHistory().getId())
        );
    }

    @NotNull
    public VerticalLayout createMediaCard(HistoryFile historyFile, Path filePath, Component component, Long historyId) {
        var vert = new VerticalLayout();
        vert.setWidth("95%");
        vert.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        vert.setClassName("custom-media-card");

        var deleteButton = new Button("Удалить файл");
        deleteButton.setIcon(VaadinIcon.FILE_REMOVE.create());
        deleteButton.addClickListener(event -> {
            var hfOp = historyFilePathsRepository.findByPath(filePath.toString());

            if (hfOp.isEmpty()) {
                return;
            }

            for (HistoryFilePath path : hfOp) {
                try {
                    var history = historyRepository.findById(historyId)
                            .orElseThrow();
                    history.setUpdatedAt(LocalDateTime.now());
                    historyRepository.saveAndFlush(history);
                    removeMedia(historyFile, path, filePath);
                } catch (IOException e) {
                    log.error("Failed to delete history file path:", e);
                }
            }
        });
        vert.add(component, deleteButton);
        return vert;
    }

    @Transactional
    public void removeMedia(HistoryFile historyFile, HistoryFilePath hfOp, Path filePath) throws IOException {
        historyFilePathsRepository.deleteById(hfOp.getId());
        historyFilesRepository.deleteById(historyFile.getId());
        if (filePath != null) {
            Files.delete(filePath.toAbsolutePath());
        }
        tasteService.taste(Notification.Position.TOP_CENTER, NotificationVariant.LUMO_SUCCESS, "Файл удален");
        media.setItems(historyRepository.findById(historyFile.getHistory().getId())
                .orElseThrow().getFiles());
        entityManager.flush();
        entityManager.clear();
        historyFile.getFilePaths().removeIf(f -> Objects.equals(f.getId(), hfOp.getId()));
    }


    @NotNull
    private Image getImage(StreamResource streamResource) {
        Image image = new Image(streamResource, "Фото");
        image.setMaxHeight("450px");
        image.setMaxWidth("430px");
        image.setHeight("auto");
        image.setWidth("auto");
        return image;
    }

    @NotNull
    private Video getVideo(StreamResource streamResource) {
        Video video = new Video(streamResource, "Видео");
        video.setClassName("video-view");
        video.setHeight("450px");
        video.setWidth("430px");
        video.setMaxHeight("450px");
        video.setMaxWidth("430px");
        return video;
    }

    @NotNull
    private StreamResource getStreamResource(String fileName, Path filePath) {
        return new StreamResource(
                fileName,
                () -> {
                    try {
                        return new FileInputStream(filePath.toFile());
                    } catch (FileNotFoundException e) {
                        log.error("Failed to open file:", e);
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    @Nullable
    private Response publishRequest(History history) {
        return restClient.post()
                .uri("/publisher")
                .body(PublisherRequest.builder()
                        .chatId(history.getUser().getTelegramChatId())
                        .historyId(history.getId())
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(Response.class);
    }

    private boolean checkHistory(History history) {
        if (history.getHistoryStatus() != HistoryStatus.ADMIN_CHECKING) {
            tasteService.taste(Notification.Position.TOP_CENTER, NotificationVariant.LUMO_ERROR,
                    "История уже была опубликована, или отклонена!");
            return true;
        }
        return false;
    }
}
