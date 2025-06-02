package xyz.khooz.chrono.persian.cldr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;




public class DataCacheTest {

    // We recommend installing an extension to run java tests.

    @Test
    public void testGetLocaleDataReturnsNonNullForPopularLocale() {

        JsonNode faNode = DataCache.getLocaleData("fa");
        assertNotNull("Locale data for 'fa' should not be null", faNode);

        JsonNode enNode = DataCache.getLocaleData("en");
        assertNotNull("Locale data for 'en' should not be null", enNode);
    }

    @Test
    public void testGetLocaleDataCachesResult() {
        JsonNode first = DataCache.getLocaleData("fa");
        JsonNode second = DataCache.getLocaleData("fa");
        assertSame("Should return the same cached instance", first, second);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLocaleDataThrowsForMissingLocale() {
        // Assumes "zz" is not a valid locale and resource is missing
        DataCache.getLocaleData("zz");
    }

    @Test
    public void testPropsConstants() {
        assertEquals("cldr", DataCache.Props.NAMESPACE);
        assertEquals("base.path", DataCache.Props.BASE_PATH);
        assertEquals("/cldr-cal-persian-full/main", DataCache.Props.DEFAULT_PATH);
        assertEquals("/cldr.properties", DataCache.Props.PROPERTIES_FILE);
        assertEquals("ca-persian.json", DataCache.Props.FILE_NAME);
    }

    @Test
    public void testBasePathIsNotNull() {
        assertNotNull("BASE_PATH should not be null", DataCache.BASE_PATH);
    }
}
