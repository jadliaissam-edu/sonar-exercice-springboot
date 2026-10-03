package com.jadli.bookstore.level5.legacy;

import java.util.Date;

public class LegacyDateUtil {

    @Deprecated
    public static int yearOf(Date date) {
        return date.getYear() + 1900;
    }

    public static Date firstDayOf(int year) {
        return new Date(year - 1900, 0, 1);
    }

    public static boolean isLeapSeason(Date date) {
        return date.getMonth() == 11;
    }
}
