package ru.eddyz.telegrambot.domain.enums;

public enum ButtonsText {
    //Replay
    PROFILE("Профиль 🧑‍💼"),
    WALLET("Кошелек 👛"),
    PAYMENTS("История платежей 💸"),
    INSURANCE("Страховка 📃"),


    //inline

    INSTALL_WALLET("Установить / Изменить номер кошелька 👛"),
    UP_BALANCE("Пополнить баланс 💵"),
    WITHDRAW_MONEY("Вывести деньги 💸"),
    CLOSE("Закрыть ❌")
    ;
    private final String cmd;

    ButtonsText(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }
}
