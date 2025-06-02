package xyz.khooz.chrono.persian;

import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalQuery;
import java.time.temporal.ValueRange;
import java.util.Locale;
import java.util.Objects;

import xyz.khooz.chrono.persian.cldr.EraLocale;

/**
 * An implementation of the Persian calendar eras for a custom Persian chronology.
 * <p>
 * This enum defines two eras:
 * <ul>
 *   <li>{@code ANTE_HIJRAH} – representing the time before Hijrah</li>
 *   <li>{@code HIJRAH} – representing the Hijrah era</li>
 * </ul>
 * Each era supports display names in three text styles (FULL, SHORT, NARROW)
 * and four languages (English, Farsi, Arabic, and French).
 * <p>
 * This type implements {@code java.time.chrono.Era} and overrides methods to support:
 * <ul>
 *   <li>{@code getValue()} – returns a numeric value (0 for ANTE_HIJRAH, 1 for HIJRAH)</li>
 *   <li>{@code isSupported(TemporalField)}</li>
 *   <li>{@code range(TemporalField)}</li>
 *   <li>{@code getLong(TemporalField)}</li>
 *   <li>{@code adjustInto(Temporal)}</li>
 *   <li>{@code query(TemporalQuery)}</li>
 * </ul>
 */
public enum Era implements java.time.chrono.Era {
    /**
     * The era representing the time before Hijrah.
     * This is conventionally represented by the value 0.
     */
    HIJRAH(0);
    /**
     * A list of all eras in the Persian calendar.
     */
    protected static final java.time.chrono.Era[] VALUES = {HIJRAH};
    /**
     * The serial version UID for serialization compatibility.
     */
    private static final long serialVersionUID = 8349273498237498234L;

    /**
     * Persian epoch corresponds to Gregorian 622-03-19 (observation-based vernal equinox).
     * But in 1925, March 20 was set as the Persian New Year (Nowruz) (calculation-based vernal equinox).
     * For this implementation, we use March 20, 622 as the epoch date; but it is important to note that
     * the Persian calendar is based on the observation of the vernal equinox, which can vary slightly.
     * This date is used as the starting point for the Persian calendar system and is accurate from 1925 CE onwards.
     */
    public static final java.time.LocalDate EPOCH = java.time.LocalDate.of(622, 3, 20);
    public static final java.time.LocalDate UNIX_EPOCH = java.time.LocalDate.of(1970, 1, 1);

    // The numeric value for the era. Conventionally, 0 represents ANTE_HIJRAH and 1 represents HIJRAH.
    private final int value;

    Era(int value) {
        this.value = value;
    }

    /**
     * Returns the numeric value associated with the era.
     * Convention: 0 for ANTE_HIJRAH, 1 for HIJRAH.
     *
     * @return the era value as an {@code int}
     */
    @Override
    public int getValue() {
        return value;
    }

    /**
     * Retrieves the textual representation of this era using the specified style and locale.
     * If a mapping for the locale is not found, it falls back to English.
     *
     * @param style  the text style, not null
     * @param locale the locale, not null
     * @return the display name for the era, never null
     * @throws NullPointerException     if either parameter is null
     * @throws IllegalArgumentException if the text style is not supported
     */
    @Override
    public String getDisplayName(TextStyle style, Locale locale) {
        Objects.requireNonNull(style, "TextStyle must not be null");
        Objects.requireNonNull(locale, "Locale must not be null");
        EraLocale eraLocale = EraLocale.of(getValue(), locale);
        if (eraLocale == null) {
            throw new IllegalArgumentException("No era data found for locale: " + locale);
        }
        return switch (style) {
            case FULL -> eraLocale.name();
            case SHORT -> eraLocale.abbreviation();
            case NARROW -> eraLocale.narrow();
            default -> throw new IllegalArgumentException("Unsupported TextStyle: " + style);
        };
    }

    /**
     * Checks if the specified field is supported.
     * This era implementation supports only the {@code ERA} field.
     *
     * @param field the field to check, null returns false
     * @return true if the field is {@code ChronoField.ERA}, false otherwise
     */

    @Override
    public boolean isSupported(java.time.temporal.TemporalField field) {
        if (field == ChronoField.ERA) {
            return true;
        }
        return field != null && field.isSupportedBy(this);
    }

    /**
     * Gets the range of valid values for the specified field.
     * For {@code ERA}, the valid range is 0 to 1.
     *
     * @param field the field for which the range is queried, not null
     * @return the range of valid values for the field
     * @throws UnsupportedOperationException if the field is not supported
     */
    @Override
    public ValueRange range(java.time.temporal.TemporalField field) {
        if (field == ChronoField.ERA) {
            return ValueRange.of(0, 1);
        }
        throw new UnsupportedOperationException("Unsupported field: " + field);
    }

    /**
     * Gets the value of the specified field from this era as a {@code long}.
     *
     * @param field the field to get, not null
     * @return the value for the field, as a {@code long}
     * @throws UnsupportedOperationException if the field is not supported
     */
    @Override
    public long getLong(java.time.temporal.TemporalField field) {
        if (field == ChronoField.ERA) {
            return getValue();
        }
        throw new UnsupportedOperationException("Unsupported field: " + field);
    }

    /**
     * Adjusts the specified temporal object to have this era.
     * This is typically used to set the {@code ERA} field.
     *
     * @param temporal the temporal object to adjust, not null
     * @return the adjusted temporal object, not null
     */
    @Override
    public Temporal adjustInto(Temporal temporal) {
        return temporal.with(ChronoField.ERA, getValue());
    }

    /**
     * Queries this era using the specified query.
     * This implementation supports queries for precision by returning the {@code ERA} field.
     *
     * @param <R>   the type returned by the query
     * @param query the query to invoke, not null
     * @return the result of the query, may be null if not supported
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R> R query(TemporalQuery<R> query) {
        Objects.requireNonNull(query, "query must not be null");
        if (query == java.time.temporal.TemporalQueries.precision()) {
            // Although some implementations return a ChronoUnit, we simply return ChronoField.ERA.
            return (R) ChronoField.ERA;
        }
        return query.queryFrom(this);
    }

    /**
     * Returns a textual representation of this era.
     * The returned string is the full display name in English.
     *
     * @return a string representation of the era, never null
     */
    @Override
    public String toString() {
        return getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }
}