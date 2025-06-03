package xyz.khooz.chrono.persian;

import java.time.temporal.ChronoField;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LocalDateTest {

    @Test
    void testConstructorAndGetters() {
        LocalDate date = new LocalDate(1403, 2, 15);
        Assertions.assertEquals(1403, date.get(ChronoField.YEAR));
        Assertions.assertEquals(2, date.get(ChronoField.MONTH_OF_YEAR));
        Assertions.assertEquals(15, date.get(ChronoField.DAY_OF_MONTH));
    }

    @Test
    void testLengthOfMonth() {
        // Months 1-6: 31 days
        for (int m = 1; m <= 6; m++) {
            LocalDate date = new LocalDate(1403, m, 1);
            Assertions.assertEquals(31, date.lengthOfMonth());
        }
        // Months 7-11: 30 days
        for (int m = 7; m <= 11; m++) {
            LocalDate date = new LocalDate(1403, m, 1);
            Assertions.assertEquals(30, date.lengthOfMonth());
        }
        // Month 12: 29 or 30 days depending on leap year
        LocalDate dateLeap = new LocalDate(1399, 12, 1); // Leap year
        LocalDate dateNonLeap = new LocalDate(1400, 12, 1); // Non-leap year
        Assertions.assertEquals(
            Chronology.INSTANCE.isLeapYear(1399) ? 30 : 29,
            dateLeap.lengthOfMonth()
        );
        Assertions.assertEquals(
            Chronology.INSTANCE.isLeapYear(1400) ? 30 : 29,
            dateNonLeap.lengthOfMonth()
        );
    }

    @Test
    void testLengthOfYear() {
        LocalDate leap = new LocalDate(1399, 1, 1);
        LocalDate nonLeap = new LocalDate(1400, 1, 1);
        Assertions.assertEquals(
            Chronology.INSTANCE.isLeapYear(1399) ? 366 : 365,
            leap.lengthOfYear()
        );
        Assertions.assertEquals(
            Chronology.INSTANCE.isLeapYear(1400) ? 366 : 365,
            nonLeap.lengthOfYear()
        );
    }

    @Test
    void testIsLeapYear() {
        LocalDate leap = new LocalDate(1399, 1, 1);
        LocalDate nonLeap = new LocalDate(1400, 1, 1);
        Assertions.assertEquals(Chronology.INSTANCE.isLeapYear(1399), leap.isLeapYear());
        Assertions.assertEquals(Chronology.INSTANCE.isLeapYear(1400), nonLeap.isLeapYear());
    }

    @Test
    void testGetDayOfYear() {
        LocalDate date = new LocalDate(1403, 2, 10);
        int expected = 31 + 10; // First month: 31 days, plus 10 days in second month
        Assertions.assertEquals(expected, date.get(ChronoField.DAY_OF_YEAR));
    }

    @Test
    void testGetDayOfWeek() {
        LocalDate date3 = new LocalDate(1403, 1, 1);
        LocalDate date6 = new LocalDate(1403, 2, 1);
        LocalDate date2 = new LocalDate(1403, 3, 1);
        LocalDate date5 = new LocalDate(1403, 4, 1);
        LocalDate date1 = new LocalDate(1403, 5, 1);
        LocalDate date4 = new LocalDate(1403, 6, 1);
        LocalDate date7 = new LocalDate(1403, 7, 1);
        LocalDate epochDate = new LocalDate(1348, 10, 11);
        Assertions.assertEquals(1, date1.get(ChronoField.DAY_OF_WEEK));
        Assertions.assertEquals(2, date2.get(ChronoField.DAY_OF_WEEK));
        Assertions.assertEquals(3, date3.get(ChronoField.DAY_OF_WEEK));
        Assertions.assertEquals(4, date4.get(ChronoField.DAY_OF_WEEK));
        Assertions.assertEquals(5, date5.get(ChronoField.DAY_OF_WEEK));
        Assertions.assertEquals(6, date6.get(ChronoField.DAY_OF_WEEK));
        Assertions.assertEquals(7, date7.get(ChronoField.DAY_OF_WEEK));
        Assertions.assertEquals(4, epochDate.get(ChronoField.DAY_OF_WEEK)); // 1348-10-11 is a Saturday
    }

    @Test
    void testIsSupportedField() {
        LocalDate date = new LocalDate(1403, 1, 1);
        Assertions.assertTrue(date.isSupported(ChronoField.YEAR));
        Assertions.assertFalse(date.isSupported((java.time.temporal.TemporalField) null));
    }

    @Test
    void testToEpochDayAndCompareTo() {
        LocalDate date1 = new LocalDate(1403, 1, 1);
        LocalDate date1eq = new LocalDate(1403, 1, 1);
        LocalDate date2 = new LocalDate(1403, 1, 2);
        java.time.LocalDate date3 = java.time.LocalDate.of(2024, 3, 20);
        java.time.LocalDate date3eq = java.time.LocalDate.of(2024, 3, 20);
        Assertions.assertTrue(date2.toEpochDay() > date1.toEpochDay());
        Assertions.assertEquals(date1.toEpochDay(), date3.toEpochDay());
        Assertions.assertEquals(date1.toEpochDay(), date1eq.toEpochDay());
        Assertions.assertEquals(date3.toEpochDay(), date3eq.toEpochDay());
        Assertions.assertEquals(0, date1.compareTo(date1eq));
        Assertions.assertEquals(0, date3.compareTo(date3eq));
        Assertions.assertTrue(date1.compareTo(date3) > 0);
        Assertions.assertTrue(date3.compareTo(date1) < 0);
        Assertions.assertTrue(date1.compareTo(date2) < 0);
        Assertions.assertTrue(date2.compareTo(date1) > 0);
        var ex = Assertions.assertThrows(IllegalArgumentException.class, () -> date1.compareTo(null));
        Assertions.assertEquals("Cannot compare to null", ex.getMessage());
    }

    @Test
    void testToEpochDay() {
        LocalDate epochDate = new LocalDate(1348, 10, 11);
        LocalDate epochDateMinus1Day = new LocalDate(1348, 10, 10);
        LocalDate epochDateMinus1Month = new LocalDate(1348, 9, 11);
        LocalDate epochDateMinus1Year = new LocalDate(1347, 10, 11);
        LocalDate epochDatePlus1Day = new LocalDate(1348, 10, 12);
        LocalDate epochDatePlus1Month = new LocalDate(1348, 11, 11);
        LocalDate epochDatePlus1Year = new LocalDate(1349, 10, 11);
        Assertions.assertEquals(0, epochDate.toEpochDay());
        Assertions.assertEquals(-1, epochDateMinus1Day.toEpochDay());
        Assertions.assertEquals(-30, epochDateMinus1Month.toEpochDay());
        Assertions.assertEquals(-365, epochDateMinus1Year.toEpochDay());
        Assertions.assertEquals(1, epochDatePlus1Day.toEpochDay());
        Assertions.assertEquals(30, epochDatePlus1Month.toEpochDay());
        Assertions.assertEquals(365, epochDatePlus1Year.toEpochDay());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDate date1 = new LocalDate(1403, 1, 1);
        LocalDate date2 = new LocalDate(1403, 1, 1);
        LocalDate date3 = new LocalDate(1403, 1, 2);
        Assertions.assertEquals(date1, date2);
        Assertions.assertNotEquals(date1, date3);
        Assertions.assertEquals(date1.hashCode(), date2.hashCode());
        Assertions.assertNotEquals(date1.hashCode(), date3.hashCode());
    }

    @Test
    void testInvalidMonthThrows() {
        var ex = Assertions.assertThrows(IllegalArgumentException.class, () -> LocalDate.of(1403, 13, 1));
        Assertions.assertEquals("Invalid date: 1403-13-01", ex.getMessage());
    }

    @Test
    void testIsValid() {
        LocalDate valid = new LocalDate(1403, 1, 1);
        LocalDate invalidMonth = new LocalDate(1403, 13, 1);
        LocalDate invalidDay = new LocalDate(1403, 1, 32);
        Assertions.assertTrue(valid.isValid());
        Assertions.assertFalse(invalidMonth.isValid());
        Assertions.assertFalse(invalidDay.isValid());
    }

    // @SuppressWarnings("all")
    // @Test
    // void testUnsupportedOperations() {
    //     LocalDate date = new LocalDate(1403, 1, 1);
    //     Exception ex;
    //     ex = Assertions.assertThrows(UnsupportedOperationException.class, () -> date.with(ChronoField.YEAR, 1404));
    //     Assertions.assertEquals("Unsupported field: YEAR", ex.getMessage());
    //     Assertions.assertThrows(UnsupportedOperationException.class, () -> date.with((t) -> date));
    //     Assertions.assertThrows(UnsupportedOperationException.class, () -> date.until(date, null));
    //     Assertions.assertThrows(UnsupportedOperationException.class, () -> date.plus(1, null));
    //     Assertions.assertThrows(UnsupportedOperationException.class, () -> date.minus(1, null));
    //     Assertions.assertThrows(UnsupportedOperationException.class, () -> date.query(null));
    //     Assertions.assertThrows(UnsupportedOperationException.class, () -> date.range(ChronoField.HOUR_OF_DAY));
    // }
}