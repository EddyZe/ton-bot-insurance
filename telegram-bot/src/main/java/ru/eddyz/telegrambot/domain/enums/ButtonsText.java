package ru.eddyz.telegrambot.domain.enums;

public enum ButtonsText {
    PROFILE("Профиль 🧑‍💼"),
    WALLET("Кошелек 👛"),
    PAYMENTS("История платежей 💸"),
    INSURANCE("Страховка 📃");
    private final String cmd;

    ButtonsText(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }
}
