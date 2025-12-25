package com.instantsolutions.larimarpharma.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class DateUtil {

    public static LocalDate calculateVisitDate(int week, int dayOfWeek) {

        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);

        // Move to first Monday
        LocalDate firstMonday = firstDayOfMonth.with(
                TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)
        );

        return firstMonday
                .plusWeeks(week - 1)
                .with(DayOfWeek.of(dayOfWeek));
    }

}
