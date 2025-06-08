package xyz.khooz.chrono.persian;

import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import xyz.khooz.chrono.persian.cldr.EraLocale;

class EraTest {

    private static Locale locale;
    private static EraLocale eraLocale;

    @BeforeAll
    @SuppressWarnings("unused")
    static void setUpAll() {
        // This method can be used to set up any common test data or configurations
        // if needed in the future.
        locale = Locale.of("fa");
        eraLocale = EraLocale.of(0, locale);
    }

    @Test
    void testGetValue() {
        assertEquals(0, Era.HIJRAH.getValue());
    }

    @Test
    void testGetDisplayNameFullEnglish() {
        // Mock EraLocale to avoid dependency on external data
        Assertions.assertEquals("هجری شمسی", eraLocale.name());
        Assertions.assertEquals("ه‍.ش.", eraLocale.abbreviation());
        Assertions.assertEquals("ه‍.ش.", eraLocale.narrow());

        Assertions.assertEquals("هجری شمسی", Era.HIJRAH.getDisplayName(TextStyle.FULL, locale));
        Assertions.assertEquals("ه‍.ش.", Era.HIJRAH.getDisplayName(TextStyle.SHORT, locale));
        Assertions.assertEquals("ه‍.ش.", Era.HIJRAH.getDisplayName(TextStyle.NARROW, locale));

    }

    @Test
    void testGetDisplayNameThrowsOnNull() {
        var ex = Assertions.assertThrows(NullPointerException.class, () -> Era.HIJRAH.getDisplayName(null, Locale.ENGLISH));
        Assertions.assertNotNull(ex);
        ex = Assertions.assertThrows(NullPointerException.class, () -> Era.HIJRAH.getDisplayName(TextStyle.FULL, null));
        Assertions.assertNotNull(ex);
    }


    @Test
    void testIsSupported() {
        TemporalField field = ChronoField.ERA;
        Assertions.assertTrue(Era.HIJRAH.isSupported(field));
        Assertions.assertTrue(field.isSupportedBy(Era.HIJRAH));
        Assertions.assertFalse(Era.HIJRAH.isSupported(null));
    }

    @Test
    void testRange() {
        TemporalField unsupported = ChronoField.YEAR; // Example of an unsupported field
        Assertions.assertEquals(ValueRange.of(0, 0), Era.HIJRAH.range(ChronoField.ERA));
        var ex = Assertions.assertThrows(UnsupportedOperationException.class, () -> Era.HIJRAH.range(unsupported));
        Assertions.assertNotNull(ex);
        Assertions.assertEquals("Unsupported field: " + unsupported, ex.getMessage());
    }

    @Test
    void testGetLong() {
        TemporalField unsupported = ChronoField.YEAR;
        Assertions.assertEquals(0L, Era.HIJRAH.getLong(ChronoField.ERA));
        var ex = Assertions.assertThrows(UnsupportedOperationException.class, () -> Era.HIJRAH.getLong(unsupported));
        Assertions.assertNotNull(ex);
        Assertions.assertEquals("Unsupported field: " + unsupported, ex.getMessage());
    }
}