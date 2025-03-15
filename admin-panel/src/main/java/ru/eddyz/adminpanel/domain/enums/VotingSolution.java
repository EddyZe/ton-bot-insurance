package ru.eddyz.adminpanel.domain.enums;

public enum VotingSolution {
    VOTING_YES("Одобрить ✅"), VOTING_NO("Отклонить ❌"),
    VOTING_SET_PRICE_YES("Одобрить с другой суммой ✅");

    private final String result;

    VotingSolution(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return this.result;
    }
}
