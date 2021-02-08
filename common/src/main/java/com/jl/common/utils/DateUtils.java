package com.jl.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {
    public static String formatterLocalDateTime(LocalDateTime date) {
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dtf2.format(date);
    }

    public static LocalDate formatterDateString(String calendar) throws ParseException {
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy/M/d");
        return LocalDate.parse(calendar,dtf2);
    }

    public static Long getTime() {
        return new Date().getTime();
    }
}
