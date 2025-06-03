package xyz.khooz.chrono.persian;

import java.time.temporal.ChronoField;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class ChronologyTest {

    @Test
    void testDateValid() {
        LocalDate date = Chronology.INSTANCE.date(1403, 1, 1);
        Assertions.assertEquals(1403, date.get(ChronoField.YEAR));
        Assertions.assertEquals(1, date.get(ChronoField.MONTH_OF_YEAR));
        Assertions.assertEquals(1, date.get(ChronoField.DAY_OF_MONTH));

        LocalDate dateEdge = Chronology.INSTANCE.date(1, 12, 31);
        Assertions.assertEquals(1, dateEdge.get(ChronoField.YEAR));
        Assertions.assertEquals(12, dateEdge.get(ChronoField.MONTH_OF_YEAR));
        Assertions.assertEquals(31, dateEdge.get(ChronoField.DAY_OF_MONTH));
    }

    @Test
    void testDateConversion() {
        java.time.LocalDate date = java.time.LocalDate.of(2025, 3, 21);
        java.time.LocalDate dateLeap = java.time.LocalDate.of(2025, 3, 20);
        java.time.LocalDate epochDate = java.time.LocalDate.of(1970, 1, 1);
        LocalDate persianDate = new LocalDate(1404, 1, 1);
        LocalDate persianLeapDate = new LocalDate(1403, 12, 30);
        LocalDate persianEpochDate = new LocalDate(1348, 10, 11);
        LocalDate convertedDate = Chronology.INSTANCE.date(date);
        LocalDate convertedEpochDate = Chronology.INSTANCE.date(epochDate);
        LocalDate convertedLeapDate = Chronology.INSTANCE.date(dateLeap);
        Assertions.assertEquals(convertedDate, persianDate);
        Assertions.assertEquals(convertedEpochDate, persianEpochDate);
        Assertions.assertEquals(convertedLeapDate, persianLeapDate);
    }
}