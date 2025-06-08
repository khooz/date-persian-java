package xyz.khooz.chrono.persian;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;
import java.util.Objects;


public class LocalDate implements ChronoLocalDate {

    final long year;
    final int month; // 1-12
    final int day; // 1-31

    private LocalDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    private LocalDate(long year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    public java.time.chrono.Chronology getChronology() {
        return Chronology.INSTANCE;
    }

    @Override
    public java.time.chrono.Era getEra() {
        return Era.HIJRAH; // Assuming HIJRAH as the only era for simplicity
    }

    @Override
    public int lengthOfMonth() {
        if (month > 0 && month < 7) {
            return 31; // Months 1-6 have 31 days
        }
        if (month > 6 && month < 12) {
            return 30; // Months 7-11 have 30 days
        }
        if (month == 12) {
            return Chronology.INSTANCE.isLeapYear(year) ? 30 : 29; // Month 12 has 30 days in leap years, otherwise 29
        }
        throw new IllegalArgumentException("Invalid month: " + month);
    }

    public static int lengthOfMonth(int month, long year) {
        if (month > 0 && month < 7) {
            return 31; // Months 1-6 have 31 days
        }
        if (month > 6 && month < 12) {
            return 30; // Months 7-11 have 30 days
        }
        if (month == 12) {
            return Chronology.INSTANCE.isLeapYear(year) ? 30 : 29; // Month 12 has 30 days in leap years, otherwise 29
        }
        throw new IllegalArgumentException("Invalid month: " + month);
    }

    @Override
    public int lengthOfYear() {
        if (Chronology.INSTANCE.isLeapYear(year)) {
            return 366; // Leap year has 366 days
        } else {
            return 365; // Non-leap year has 365 days
        }
    }

    @Override
    public boolean isLeapYear() {
        // Implement leap year logic for Persian calendar
        return Chronology.INSTANCE.isLeapYear(year);
    }

    @Override
    public int get(TemporalField field) {
        if (!(field instanceof ChronoField chronofield) || !chronofield.isDateBased()) {
            throw new UnsupportedOperationException("Field not supported: " + field);
        }
        return switch(chronofield) {
            case YEAR -> (int) year;
            case MONTH_OF_YEAR -> month;
            case DAY_OF_MONTH -> day;
            case DAY_OF_YEAR -> {
                int dayOfYear = 0;
                for (int m = 1; m < month; m++) {
                    dayOfYear += lengthOfMonth(month, year);
                }
                dayOfYear += day;
                yield dayOfYear;
            }
            case DAY_OF_WEEK -> {
                // Calculate day of week using Zeller's Congruence or similar algorithm, I used JDN convention
                // Convert to Julian Day Number (JDN) for Persian calendar
                long julianDayNumber = toEpochDay(); // + 1948440; // Convert to Julian Day Number
                // Adjust for Persian calendar offset
                yield (int) ((julianDayNumber + 3) % 7) + 1;
            }
            case ALIGNED_WEEK_OF_MONTH -> {
                int week = (day - 1) / 7 + 1; // Weeks start from 1
                yield week;
            }
            case ALIGNED_WEEK_OF_YEAR -> {
                int week = (get(ChronoField.DAY_OF_YEAR) - 1) / 7 + 1; // Weeks start from 1
                yield week;
            }
            case ERA -> 0; // Assuming only one era (HIJRAH) for simplicity
            case YEAR_OF_ERA -> (int) year; // In Persian calendar, year of era is the same as year
            case EPOCH_DAY -> (int) toEpochDay(); // Convert to epoch day
            case INSTANT_SECONDS -> throw new UnsupportedOperationException("INSTANT_SECONDS not supported for LocalDate");
            case MICRO_OF_DAY, NANO_OF_DAY, HOUR_OF_DAY, MINUTE_OF_HOUR, SECOND_OF_MINUTE, NANO_OF_SECOND, MICRO_OF_SECOND -> throw new UnsupportedOperationException("Field not supported: " + field);
            case HOUR_OF_AMPM, AMPM_OF_DAY, CLOCK_HOUR_OF_AMPM, CLOCK_HOUR_OF_DAY -> throw new UnsupportedOperationException("Time fields not supported for LocalDate");
            case OFFSET_SECONDS -> throw new UnsupportedOperationException("Offset fields not supported for LocalDate");
            default -> throw new UnsupportedOperationException("Field not supported: " + field);
        };
    }

    @Override
    public long getLong(TemporalField field) {
        if (!(field instanceof ChronoField chronofield) || !chronofield.isDateBased()) {
            throw new UnsupportedOperationException("Field not supported: " + field);
        }
        return switch(chronofield) {
            case YEAR -> year;
            case MONTH_OF_YEAR -> month;
            case DAY_OF_MONTH -> day;
            case DAY_OF_YEAR -> {
                int dayOfYear = 0;
                for (int m = 1; m < month; m++) {
                    dayOfYear += lengthOfMonth(month, year);
                }
                dayOfYear += day;
                yield dayOfYear;
            }
            case DAY_OF_WEEK -> {
                // Calculate day of week using Zeller's Congruence or similar algorithm, I used JDN convention
                // Convert to Julian Day Number (JDN) for Persian calendar
                long julianDayNumber = toEpochDay(); // + 1948440; // Convert to Julian Day Number
                // Adjust for Persian calendar offset
                yield ((julianDayNumber + 3) % 7) + 1;
            }
            case ALIGNED_WEEK_OF_MONTH -> (day - 1) / 7 + 1; // Weeks start from 1
            case ALIGNED_WEEK_OF_YEAR -> (get(ChronoField.DAY_OF_YEAR) - 1) / 7 + 1; // Weeks start from 1
            case ERA -> 0; // Assuming only one era (HIJRAH) for simplicity
            case YEAR_OF_ERA -> year; // In Persian calendar, year of era is the same as year
            case PROLEPTIC_MONTH -> (year - 1) * 12 + month; // Proleptic month calculation
            case EPOCH_DAY -> toEpochDay(); // Convert to epoch day
            case INSTANT_SECONDS -> throw new UnsupportedOperationException("INSTANT_SECONDS not supported for LocalDate");
            case MICRO_OF_DAY, NANO_OF_DAY, HOUR_OF_DAY, MINUTE_OF_HOUR, SECOND_OF_MINUTE, NANO_OF_SECOND, MICRO_OF_SECOND -> throw new UnsupportedOperationException("Field not supported: " + field);
            case HOUR_OF_AMPM, AMPM_OF_DAY, CLOCK_HOUR_OF_AMPM, CLOCK_HOUR_OF_DAY -> throw new UnsupportedOperationException("Time fields not supported for LocalDate");
            case OFFSET_SECONDS -> throw new UnsupportedOperationException("Offset fields not supported for LocalDate");
            default -> throw new UnsupportedOperationException("Field not supported: " + field);
        };
    }

    @Override
    public boolean isSupported(TemporalField field) {
        if (!(field instanceof ChronoField chronofield) || !chronofield.isDateBased()) {
            return false;
        }
        return switch(chronofield) {
            case YEAR, MONTH_OF_YEAR, DAY_OF_MONTH,
                DAY_OF_YEAR, DAY_OF_WEEK, ALIGNED_WEEK_OF_MONTH,
                ALIGNED_WEEK_OF_YEAR, ERA, YEAR_OF_ERA,
                PROLEPTIC_MONTH, EPOCH_DAY -> true;
            default -> false;
        };
    }

    @Override
    public boolean isSupported(TemporalUnit unit) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ChronoLocalDate with(TemporalField field, long newValue) {
        if (!(field instanceof ChronoField chronofield) || !chronofield.isDateBased()) {
            throw new UnsupportedOperationException("Field not supported: " + field);
        }
        return switch(chronofield) {
            case YEAR -> LocalDate.of(newValue, month, day);
            case MONTH_OF_YEAR -> {
                if (newValue < 1 || newValue > 12) {
                    throw new IllegalArgumentException("Invalid month: " + newValue);
                }
                yield LocalDate.of(year, (int) newValue, day);
            }
            case DAY_OF_MONTH -> {
                if (newValue < 1 || newValue > lengthOfMonth()) {
                    throw new IllegalArgumentException("Invalid day of month: " + newValue);
                }
                yield LocalDate.of(year, month, (int) newValue);
            }
            default -> throw new UnsupportedOperationException("Field not supported: " + field);
        };
    }

    @Override
    public ChronoLocalDate with(TemporalAdjuster adjuster) {
        if (adjuster == null) {
            throw new IllegalArgumentException("Adjuster cannot be null");
        }
        // optimizations borrowed from java.time.LocalDate
        if (adjuster instanceof LocalDate localDate) {
            return localDate;
        }
        return (LocalDate) adjuster.adjustInto(this);
    }


    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ChronoLocalDate plus(long amountToAdd, TemporalUnit unit) {
        if (!(unit instanceof ChronoUnit chronoUnit)) {
            throw new UnsupportedOperationException("Unit not supported: " + unit);
        }
        return switch (chronoUnit) {
            case DAYS -> LocalDate.ofEpochDay(toEpochDay() + amountToAdd);
            case WEEKS -> LocalDate.ofEpochDay(toEpochDay() + amountToAdd * 7);
            case MONTHS -> {
                long adjustment = (year * 12 + month - 1 + amountToAdd);
                int monthIntermediate = (int) (adjustment % 12);
                if (monthIntermediate < 0) {
                    monthIntermediate += 12; // Adjust to ensure month is positive
                }
                int newMonth = monthIntermediate + 1;
                long newYear = year + (adjustment % 12 < 0 ? adjustment / 12 - 1 : adjustment / 12);
                yield LocalDate.of(newYear, newMonth, day);
            }
            case YEARS -> LocalDate.of(year + amountToAdd, month, day);
            default -> throw new UnsupportedOperationException("Unit not supported: " + unit);
        };
    }

    @Override
    public ChronoLocalDate minus(long amountToSubtract, TemporalUnit unit) {
        return plus(-amountToSubtract, unit);
    }

    @Override
    public long toEpochDay() {
        // Convert Persian date to epoch day
        // This is a simplified version, actual conversion may vary based on the calendar system
        if (!isValid()) throw new IllegalArgumentException("Invalid date: " + year + "-" + month + "-" + day);
        long days = calculateDaysFromYears(year);
        days += calculateDaysFromMonths(year, month);
        days += day;
        return days + Chronology.PERSIAN_EPOCH_OFFSET;
    }

    private long calculateDaysFromYears(long year) {
        long days = 0;
        if (year > 0) {
            for (int y = 1; y < year; y++) {
                days += Chronology.INSTANCE.isLeapYear(y) ? 366 : 365;
            }
        } else {
            for (int y = -1; y >= year; y--) {
                days -= Chronology.INSTANCE.isLeapYear(y) ? 366 : 365;
            }
        }
        return days;
    }

    private long calculateDaysFromMonths(long year, int month) {
        long days = 0;
        if (year > 0) {
            for (int m = 1; m < month; m++) {
                days += lengthOfMonth(m, year);
            }
        } else {
            for (int m = 12; m >= month; m--) {
                days -= lengthOfMonth(m, year);
            }
        }
        return days;
    }

    @Override
    public int compareTo(ChronoLocalDate other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot compare to null");
        }
        if (!(other instanceof ChronoLocalDate)) {
            throw new ClassCastException("Cannot compare " + other.getClass() + " with LocalDate");
        }
        int epochCompare = Long.compare(toEpochDay(), other.toEpochDay());
        if (epochCompare != 0) {
            return epochCompare;
        }
        return getChronology().getId().compareTo(other.getChronology().getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ChronoLocalDate other)) {
            return false;
        }
        return compareTo(other) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(toEpochDay(), getChronology());
    }

    @Override
    public String toString() {
        // Return ISO format YYYY-MM-DD date string
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    @Override
    public ValueRange range(TemporalField field) {
        if (!(field instanceof ChronoField chronofield) || !chronofield.isDateBased()) {
            throw new UnsupportedOperationException("Field not supported: " + field);
        }
        return switch(chronofield) {
            case YEAR -> ValueRange.of(-9999, 9999);
            case MONTH_OF_YEAR -> ValueRange.of(1, 12);
            case DAY_OF_MONTH -> ValueRange.of(1, lengthOfMonth());
            case DAY_OF_YEAR -> ValueRange.of(1, lengthOfYear());
            case DAY_OF_WEEK -> ValueRange.of(1, 7);
            case ALIGNED_WEEK_OF_MONTH -> ValueRange.of(1, 5); // Assuming max 5 weeks in a month
            case ALIGNED_WEEK_OF_YEAR -> ValueRange.of(1, 53); // Assuming max 53 weeks in a year
            case ERA -> ValueRange.of(0, 0); // Only one era (HIJRAH)
            case YEAR_OF_ERA -> ValueRange.of(1, 9999); // Year of era is always positive
            case PROLEPTIC_MONTH -> ValueRange.of(Long.MIN_VALUE, Long.MAX_VALUE);
            default -> throw new UnsupportedOperationException("Field not supported: " + field);
        };
    }

    @Override
    public ChronoPeriod until(ChronoLocalDate endDateExclusive) {
        if (endDateExclusive == null) {
            throw new IllegalArgumentException("End date cannot be null");
            
        }
        ChronoLocalDate startDate = java.time.LocalDate.ofEpochDay(this.toEpochDay());
        ChronoLocalDate endDate = java.time.LocalDate.ofEpochDay(endDateExclusive.toEpochDay());
        return startDate.until(endDate);
    }

    public static ChronoLocalDate now(Clock clock) {
        return Chronology.INSTANCE.dateNow(clock);
    }

    public boolean isValid() {
        LocalDate date = new LocalDate(year, month, day);
        return date.range(ChronoField.YEAR).isValidIntValue(year)
            && date.range(ChronoField.MONTH_OF_YEAR).isValidIntValue(month)
            && date.range(ChronoField.DAY_OF_MONTH).isValidIntValue(day);
    }

    public static LocalDate of(int year, int month, int day) {
        LocalDate date = new LocalDate(year, month, day);
        if (!date.isValid()) {
            throw new IllegalArgumentException("Invalid date: " + date);
        }
        return date;
    }

    public static LocalDate of(long year, int month, int day) {
        LocalDate date = new LocalDate(year, month, day);
        if (!date.isValid()) {
            throw new IllegalArgumentException("Invalid date: " + date);
        }
        return date;
    }

    public static LocalDate ofEpochDay(long epochDay) {
        return Chronology.INSTANCE.dateEpochDay(epochDay);
    }

    /**
     * Obtains an instance of {@code LocalDate} from a temporal object.
     * <p>
     * This obtains a local date based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code LocalDate}.
     * <p>
     * The conversion uses the {@link TemporalQueries#localDate()} query, which relies
     * on extracting the {@link ChronoField#EPOCH_DAY EPOCH_DAY} field.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code LocalDate::from}.
     *
     * @param temporal  the temporal object to convert, not null
     * @return the local date, not null
     * @throws DateTimeException if unable to convert to a {@code LocalDate}
     */
    public static LocalDate from(TemporalAccessor temporal) {
        Objects.requireNonNull(temporal, "temporal");
        LocalDate date = temporal.<LocalDate>query(new TemporalQuery<>() {
        @Override
        public LocalDate queryFrom(TemporalAccessor temporal) {
            if (temporal.isSupported(ChronoField.EPOCH_DAY)) {
                return LocalDate.ofEpochDay(temporal.getLong(ChronoField.EPOCH_DAY));
            }
            return null;
        }

        @Override
        public String toString() {
            return "LocalDate";
        }
    });
        if (date == null) {
            throw new DateTimeException("Unable to obtain LocalDate from TemporalAccessor: " +
                    temporal + " of type " + temporal.getClass().getName());
        }
        return date;
    }

    /**
     * Obtains an instance of {@code LocalDate} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a date.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed local date, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static LocalDate parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.<LocalDate>parse(text, LocalDate::from);
    }

    /**
     * Obtains an instance of {@code LocalDate} from a text string such as {@code 2007-12-03}.
     * <p>
     * The string must represent a valid date and is parsed using
     * {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE}.
     *
     * @param text  the text to parse such as "2007-12-03", not null
     * @return the parsed local date, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static LocalDate parse(CharSequence text) {
        return parse(text, DateTimeFormatter.ISO_LOCAL_DATE
            // .withResolverStyle(ResolverStyle.STRICT)
            // .withChronology(Chronology.INSTANCE)
        );
    }
}