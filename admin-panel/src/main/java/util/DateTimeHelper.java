package util;

import java.time.format.DateTimeFormatter;

public class DateTimeHelper {


    public static DateTimeFormatter defaultDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static DateTimeFormatter getDefaultDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

}
