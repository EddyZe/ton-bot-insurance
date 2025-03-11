package ru.eddyz.telegrambot.domain.enums;

public enum ButtonsText {
    //Replay
    PROFILE("Профиль 🧑‍💼"),
    WALLET("Кошелек 👛"),
    PAYMENTS("История платежей 💸"),
    INSURANCE("Страховка 📃"),
    ADD_HISTORY("Отправить историю ➕"),
    MY_HISTORY("Мои истории 📁"),


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
    HISTORY_INSURANCE("История покупок страховки 📂"),

    /// /////////////
    PUBLISH("Опубликовать ➕"),
    HISTORY_PRICE("Установить сумму выплаты 💵"),
    HISTORY_FILES("Посмотреть все файлы 📂"),
    EDIT("Редактировать 📃"),
    REMOVE("Удалить ❌"),

    RESULT_VOTES("Посмотреть результаты голосования 🔢")

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
