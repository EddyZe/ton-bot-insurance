package ru.eddyz.telegrambot.domain.enums;

public enum HistoryStatus {

    PUBLISH("Опубликована"),
    DECLINE("Отклонена"),
    AWAITING("На проверке");

    private final String status;

    HistoryStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
