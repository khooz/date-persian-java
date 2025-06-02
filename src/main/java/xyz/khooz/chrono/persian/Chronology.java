package xyz.khooz.chrono.persian;

import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.chrono.AbstractChronology;
import java.time.chrono.ChronoLocalDate;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.Map;

@SuppressWarnings("java:S6548")
public final class Chronology  extends AbstractChronology implements Serializable {
    /**
     * Singleton instance of the PersianChronology.
     */
    public static final Chronology INSTANCE = new Chronology();
    
    // Assign a random long value to serialVersionUID
    private static final long serialVersionUID = 8810119543254328219L;
    
    // private static final int PERSIAN_EPOCH_OFFSET = 226899; // Offset for Persian epoch (March 20, 622)
    // static final int PERSIAN_EPOCH_OFFSET = 492268; // Offset for Persian epoch (March 20, 622)
    static final long PERSIAN_EPOCH_OFFSET = Era.UNIX_EPOCH.until(Era.EPOCH, ChronoUnit.DAYS); // Offset for Persian epoch (March 20, 622)
    static final int PERSIAN_MONTHS_IN_YEAR = 12; // Number of months in a Persian year
    static final int PERSIAN_DAYS_IN_MONTH = 31; // Maximum days in a month in the Persian calendar
    static final int PERSIAN_DAYS_IN_SHORT_MONTH = 30; // Days in a short month in the Persian calendar
    static final int PERSIAN_DAYS_IN_LONG_MONTH = 31; // Days in a long month in the Persian calendar
    // private static final int PERSIAN_DAYS_IN_YEAR = 365; // Days in a non-leap Persian year
    static final int PERSIAN_DAYS_IN_LEAP_YEAR = 366; // Days in a leap Persian year
    static final double PERSIAN_YEAR_LENGTH_DAYS = 365.24219858156028368; // Average length of a Persian year in days
    static final double PERSIAN_LEAP_YEAR_FRACTION = 0.24219858156028368; // Fraction for leap year calculation
    /**
     * Private constructor to prevent instantiation.
     * Use {@link #INSTANCE} to access the singleton instance.
     */
    private Chronology() {}

    @Override
    public String getId() {
        return "Persian";
    }

    @Override
    public String getCalendarType() {
        return "persian";
    }

    @Override
    public LocalDate date(int prolepticYear, int month, int dayOfMonth) {
        return new LocalDate(prolepticYear, month, dayOfMonth);
    }

    @Override
    public LocalDate dateYearDay(int prolepticYear, int dayOfYear) {
        if (dayOfYear < 1 || dayOfYear > PERSIAN_DAYS_IN_LEAP_YEAR) {
            throw new DateTimeException("Day of year must be between 1 and 366");
        }
        int month = dayOfYear < 186 ? dayOfYear / PERSIAN_DAYS_IN_MONTH + 1 : (dayOfYear - 186) / PERSIAN_DAYS_IN_SHORT_MONTH + 7;
        int dayOfMonth = dayOfYear - (month <= 6 ? (month - 1) * PERSIAN_DAYS_IN_MONTH : (month - 7) * PERSIAN_DAYS_IN_SHORT_MONTH + 186);
        return new LocalDate(prolepticYear, month, dayOfMonth);
    }

    @Override
    public LocalDate dateEpochDay(long epochDay) throws DateTimeException {
        long daysSinceEpoch = epochDay + PERSIAN_EPOCH_OFFSET; // Adjust for Persian epoch (March 20, 622)
        if (daysSinceEpoch < 0) {
            throw new DateTimeException("Epoch day must be non-negative");
        }
        // Calculate the proleptic year, month, and day of month
        int year = (int) (daysSinceEpoch / PERSIAN_YEAR_LENGTH_DAYS) + 1; // Approximate year calculation
        int daysInYear = (int) (daysSinceEpoch % PERSIAN_YEAR_LENGTH_DAYS);
        int month = (daysInYear < 186) ? (daysInYear / PERSIAN_DAYS_IN_MONTH + 1) : ((daysInYear - 186) / PERSIAN_DAYS_IN_SHORT_MONTH + 7);
        int dayOfMonth = daysInYear - (month <= 6 ? (month - 1) * PERSIAN_DAYS_IN_MONTH : (month - 7) * PERSIAN_DAYS_IN_SHORT_MONTH + 186);
        if (dayOfMonth < 1 || dayOfMonth > PERSIAN_DAYS_IN_LONG_MONTH) {
            throw new DateTimeException("Invalid day of month: " + dayOfMonth);
        }
        if (month < 1 || month > PERSIAN_MONTHS_IN_YEAR) {
            throw new DateTimeException("Invalid month: " + month);
        }
        if (year < 1 || year > 9999) {
            throw new DateTimeException("Year out of range: " + year);
        }
        return new LocalDate(year, month, dayOfMonth);
    }

    @Override
    public LocalDate date(TemporalAccessor temporal) {
        return dateEpochDay(temporal.getLong(ChronoField.EPOCH_DAY));
    }

    @Override
    public ChronoLocalDate dateNow() {
        return dateNow(Clock.systemDefaultZone());
    }

    @Override
    public ChronoLocalDate dateNow(ZoneId zone) {
        return dateNow(Clock.system(zone));
    }

    @Override
    public ChronoLocalDate dateNow(Clock clock) {
        return date(LocalDate.now(clock));
    }

    @Override
    public boolean isLeapYear(long prolepticYear) {
        return (((prolepticYear + 2346) * PERSIAN_LEAP_YEAR_FRACTION) % 1) < PERSIAN_LEAP_YEAR_FRACTION;
    }

    @Override
    public int prolepticYear(java.time.chrono.Era era, int yearOfEra) {
        return switch (era) {
            case Era.HIJRAH -> yearOfEra; // Proleptic year is the same as year of era
            default -> throw new DateTimeException("Invalid era: " + era);
        };
    }

    @Override
    public java.time.chrono.Era eraOf(int eraValue) {
        return switch (eraValue) {
            case 0 -> Era.HIJRAH;
            default -> throw new DateTimeException("Invalid era value: " + eraValue);
        };
    }

    @Override
    public List<java.time.chrono.Era> eras() {
        return List.of(Era.values());
    }

    @Override
    public ValueRange range(ChronoField field) {
        return switch (field) {
            case YEAR_OF_ERA -> ValueRange.of(1, 9999);
            case YEAR -> ValueRange.of(-9999, 9999);
            case MONTH_OF_YEAR -> ValueRange.of(1, 12);
            case DAY_OF_MONTH -> ValueRange.of(1, 31);
            case DAY_OF_YEAR -> ValueRange.of(1, 366);
            case ERA -> ValueRange.of(0, 1);
            case DAY_OF_WEEK -> ValueRange.of(1, 7);
            case ALIGNED_WEEK_OF_MONTH -> ValueRange.of(1, 6);
            case ALIGNED_WEEK_OF_YEAR -> ValueRange.of(1, 53);
            default -> throw new UnsupportedOperationException("Unsupported field: " + field);
        };
    }

    @SuppressWarnings("java:S6205")
    @Override
    public LocalDate resolveDate(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        if (fieldValues == null || fieldValues.isEmpty()) {
            throw new DateTimeException("Field values cannot be null or empty");
        }
        if (!fieldValues.containsKey(ChronoField.YEAR) || !fieldValues.containsKey(ChronoField.MONTH_OF_YEAR) || !fieldValues.containsKey(ChronoField.DAY_OF_MONTH)) {
            throw new DateTimeException("Year, month, and day of month must be provided");
        }
        int year = Math.toIntExact(fieldValues.get(ChronoField.YEAR));
        int month = Math.toIntExact(fieldValues.get(ChronoField.MONTH_OF_YEAR));
        int dayOfMonth = Math.toIntExact(fieldValues.get(ChronoField.DAY_OF_MONTH));
        if (year < 1 || year > 9999) {
            throw new DateTimeException("Year out of range: " + year);
        }
        if (month < 1 || month > 12) {
            throw new DateTimeException("Month out of range: " + month);
        }
        if (dayOfMonth < 1 || dayOfMonth > 31) {
            throw new DateTimeException("Day of month out of range: " + dayOfMonth);
        }
        // Additional validation for month lengths can be added here
        if ((month == 2 && dayOfMonth > 29) || (month > 2 && dayOfMonth > 31)) {
            throw new DateTimeException("Invalid day of month for the specified month: " + dayOfMonth);
        }
        // Create and return a PersianDate instance
        LocalDate date = new LocalDate(year, month, dayOfMonth);
        // If resolver style is STRICT, validate the date
        return switch (resolverStyle) {
            case SMART -> {
                // In SMART mode, we can allow some leniency in date validation
                yield date;
            }
            case LENIENT -> {
                // In LENIENT mode, we can allow even more leniency
                yield date;
            }
            case STRICT -> {
                // In STRICT mode, we need to ensure the date is valid
                if (!date.isValid()) {
                    throw new DateTimeException("Invalid date: " + date);
                }
                yield date;
            }
            default -> throw new DateTimeException("Unknown resolver style: " + resolverStyle);
        };
    }
}
