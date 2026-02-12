package com.instantsolutions.larimarpharma.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class DateUtil {

    public static LocalDate calculateVisitDate(int week, int dayOfWeek) {

        // First day of NEXT month
        LocalDate firstDayOfNextMonth = LocalDate.now()
//                .plusMonths(1)
                .withDayOfMonth(1);

        // First Monday of next month
        LocalDate firstMonday = firstDayOfNextMonth.with(
                TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)
        );

        return firstMonday
                .plusWeeks(week - 1)
                .with(DayOfWeek.of(dayOfWeek));
    }

    public static LocalDate getStartOfTheMonth(){
        return LocalDate.now()
//                .plusMonths(1)
                .withDayOfMonth(1);
    }


}
