package ru.eddyz.telegrambot.domain.enums;

import lombok.Getter;

@Getter
public enum HistoryStatus {

    PUBLISH("Опубликована"),
    DECLINE("Отклонена"),
    AWAITING_PUBLISH("Ожидает публикации"),
    AWAITING_APPROVED("Ожидает окончательного решения"),
    ADMIN_CHECKING("Проходит модерацию");

    private final String status;

    HistoryStatus(String status) {
        this.status = status;
    }

}
