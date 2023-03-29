package org.peter;

import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.ThaiBuddhistDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Set;

//https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/chrono/package-summary.html

public class ChronoTest {
    public static void main(String[] args) {
        Set<Chronology> chronos = Chronology.getAvailableChronologies();
        for (Chronology chrono : chronos) {
            ChronoLocalDate date = chrono.dateNow();
            System.out.printf("   %20s: %s%n", chrono.getId(), date.toString());
        }


        // Print the Thai Buddhist date
        ChronoLocalDate now1 = Chronology.of("ThaiBuddhist").dateNow();
        int day = now1.get(ChronoField.DAY_OF_MONTH);
        int dow = now1.get(ChronoField.DAY_OF_WEEK);
        int month = now1.get(ChronoField.MONTH_OF_YEAR);
        int year = now1.get(ChronoField.YEAR);
        System.out.printf("  Today is %s %s %d-%s-%d%n", now1.getChronology().getId(),
                dow, day, month, year);
        // Print today's date and the last day of the year for the Thai Buddhist Calendar.
        ChronoLocalDate first = now1
                .with(ChronoField.DAY_OF_MONTH, 1)
                .with(ChronoField.MONTH_OF_YEAR, 1);
        ChronoLocalDate last = first
                .plus(1, ChronoUnit.YEARS)
                .minus(1, ChronoUnit.DAYS);
        System.out.printf("  %s: 1st of year: %s; end of year: %s%n", last.getChronology().getId(),
                first, last);


        // Print the Thai Buddhist date
        now1 = ThaiBuddhistDate.now();
        day = now1.get(ChronoField.DAY_OF_MONTH);
        dow = now1.get(ChronoField.DAY_OF_WEEK);
        month = now1.get(ChronoField.MONTH_OF_YEAR);
        year = now1.get(ChronoField.YEAR);
        System.out.printf("  Today is %s %s %d-%s-%d%n", now1.getChronology().getId(),
                dow, day, month, year);

        // Print today's date and the last day of the year for the Thai Buddhist Calendar.
        first = now1
                .with(ChronoField.DAY_OF_MONTH, 1)
                .with(ChronoField.MONTH_OF_YEAR, 1);
        last = first
                .plus(1, ChronoUnit.YEARS)
                .minus(1, ChronoUnit.DAYS);
        System.out.printf("  %s: 1st of year: %s; end of year: %s%n", last.getChronology().getId(),
                first, last);
    }
}

class C {

    interface B {
        int a = 12;
    }
}


interface B {
    int a();
}

class C1 extends C {
    private C1() {

    }
}

