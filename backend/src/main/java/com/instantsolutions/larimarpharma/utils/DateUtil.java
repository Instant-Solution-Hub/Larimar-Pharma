package com.instantsolutions.larimarpharma.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

public class DateUtil {

//public static LocalDate calculateVisitDate(int week, int dayOfWeek) {
//
//    LocalDate firstDayOfMonth = LocalDate.now()
//            .plusMonths(1)
//            .withDayOfMonth(1);
//
//    int totalDaysToAdd = (week - 1) * 7 + (dayOfWeek - 1);
//
//    return firstDayOfMonth.plusDays(totalDaysToAdd);
//}



    public static LocalDate calculateVisitDate(int week, int dayOfWeek) {

        // First day of next month
        LocalDate firstDayOfNextMonth = LocalDate.now()
                .plusMonths(1)
                .withDayOfMonth(1);

        // Find the Sunday on or before the 1st
        LocalDate calendarStart = firstDayOfNextMonth
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        // Calculate offset from calendar start
        int totalDaysToAdd = (week - 1) * 7 + (dayOfWeek - 1);

        return calendarStart.plusDays(totalDaysToAdd);
    }

// Production Date calculation code
//public static LocalDate calculateVisitDate(int week, int dayOfWeek) {
//
//    LocalDate firstDayOfNextMonth = LocalDate.now()
//            .plusMonths(1)
//            .withDayOfMonth(1);
//
//    int totalDaysToAdd = (week - 1) * 7 + (dayOfWeek - 1);
//
//    return firstDayOfNextMonth.plusDays(totalDaysToAdd);
//}


    public static LocalDate getStartOfTheMonth(){
        return LocalDate.now()
                .plusMonths(1)
                .withDayOfMonth(1);
    }


}
