package ru.eddyz.telegrambot.domain.enums;

public enum ButtonsText {
    //Replay
    PROFILE("Профиль 🧑‍💼"),
    WALLET("Кошелек 👛"),
    PAYMENTS("История платежей 💸"),
    INSURANCE("Страховка 📃"),
    ADD_HISTORY("Отправить историю ➕"),


    //inline

    INSTALL_WALLET("Установить / Изменить номер кошелька 👛"),
    UP_BALANCE("Пополнить баланс 💵"),
    WITHDRAW_MONEY("Вывести деньги 💸"),
    CLOSE("Закрыть ❌"),
    WITHDRAW_MONEY_HISTORY("История снятий 📃"),

    //pages
    NEXT_BUTTON("Далее ⏩"),
    PREV_BUTTON("Назад ⏪"),

    //insurance
    BUY_INSURANCE("Купить страховку %s %s 💵"),
    HISTORY_INSURANCE("История покупок страховки 📂");
    private final String cmd;

    ButtonsText(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }
}
