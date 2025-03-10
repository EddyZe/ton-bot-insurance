package ru.eddyz.telegrambot.util;

import java.util.HashMap;
import java.util.Map;

public class DataStore {


    public static Map<Long, Enum<?>> currentCommand = new HashMap<>();

    public static Map<Long, Integer> currentPageHistoryWithdraw = new HashMap<>();

    public static Map<Long, Integer> currentPageHistoryPayments = new HashMap<>();

    public static Map<Long, Integer> currentPageHistoryInsurance = new HashMap<>();

}
