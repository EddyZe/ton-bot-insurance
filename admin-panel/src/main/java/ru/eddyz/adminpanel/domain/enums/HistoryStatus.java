package ru.eddyz.adminpanel.domain.enums;

import lombok.Getter;

@Getter
public enum HistoryStatus {

    PUBLISH("Опубликована"),
    APPROVED("Одобрена"),
    DECLINE("Отклонена"),
    AWAITING_PUBLISH("Ожидает публикации"),
    AWAITING_APPROVED("Ожидает окончательного решения"),
    ADMIN_CHECKING("Проходит модерацию");

    private final String status;

    HistoryStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }

}
