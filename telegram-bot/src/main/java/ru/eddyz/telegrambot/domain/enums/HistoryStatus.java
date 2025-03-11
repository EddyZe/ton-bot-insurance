package ru.eddyz.telegrambot.domain.enums;

import lombok.Getter;

@Getter
public enum HistoryStatus {

    PUBLISH("Опубликована"),
    DECLINE("Отклонена"),
    AWAITING_PUBLISH("Ожидает публикации"),
    AWAITING_APPROVED("Ожидает окончательного решения");

    private final String status;

    HistoryStatus(String status) {
        this.status = status;
    }

}
