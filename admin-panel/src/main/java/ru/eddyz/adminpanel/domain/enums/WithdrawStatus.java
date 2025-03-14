package ru.eddyz.adminpanel.domain.enums;

public enum WithdrawStatus {

    APPROVED("Подтвержден"),
    DECLINE("Отклонен"),
    AWAITING("Ожидает решения");

    private final String status;

    WithdrawStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
