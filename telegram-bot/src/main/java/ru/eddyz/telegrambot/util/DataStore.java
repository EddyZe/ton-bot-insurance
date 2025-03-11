package ru.eddyz.telegrambot.util;

import java.util.HashMap;
import java.util.Map;

public class DataStore {


    public static Map<Long, String> currentCommand = new HashMap<>();

    public static Map<Long, Integer> currentPageHistoryWithdraw = new HashMap<>();

    public static Map<Long, Integer> currentPageHistoryPayments = new HashMap<>();

    public static Map<Long, Integer> currentPageHistoryInsurance = new HashMap<>();

    public static Map<Long, Integer> currentPageHistorySurcharge = new HashMap<>();

}
