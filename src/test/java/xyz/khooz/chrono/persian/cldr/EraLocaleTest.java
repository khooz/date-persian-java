package xyz.khooz.chrono.persian.cldr;

import java.util.Locale;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;






public class EraLocaleTest {

    private Locale testLocale;
    private String localeCode;

    @Before
    public void setUp() {
        testLocale = Locale.forLanguageTag("fa");
        localeCode = testLocale.toLanguageTag();
    }

    @After
    public void tearDown() {
        // Reset any static mocks if needed
    }

    @Test
    public void testTraverseToEraReturnsCorrectNode() {
        // Build nested structure
        ObjectNode erasNode = JsonNodeFactory.instance.objectNode();
        ObjectNode persianNode = JsonNodeFactory.instance.objectNode().set("eras", erasNode);
        ObjectNode calendarsNode = JsonNodeFactory.instance.objectNode().set("persian", persianNode);
        ObjectNode datesNode = JsonNodeFactory.instance.objectNode().set("calendars", calendarsNode);
        ObjectNode localeNode = JsonNodeFactory.instance.objectNode().set("dates", datesNode);
        ObjectNode mainNode = JsonNodeFactory.instance.objectNode().set(localeCode, localeNode);
        ObjectNode root = JsonNodeFactory.instance.objectNode().set("main", mainNode);

        JsonNode result = EraLocaleTestHelper.traverseToEraProxy(root, localeCode);
        assertSame(erasNode, result);
    }

    @Test
    public void testTraverseToEraReturnsMissingNodeIfPathIncomplete() {
        ObjectNode root = JsonNodeFactory.instance.objectNode(); // empty node
        JsonNode result = EraLocaleTestHelper.traverseToEraProxy(root, localeCode);
        assertTrue(result.isMissingNode() || result.isNull() || result.isObject() && result.size() == 0);
    }

    @Test
    public void testEraLocaleOfReturnsCorrectRecord() {
        EraLocale era = EraLocale.of(0, testLocale);
        assertEquals(0, era.value());
        assertEquals("هجری شمسی", era.name());
        assertEquals("ه‍.ش.", era.abbreviation());
        assertEquals("ه‍.ش.", era.narrow());
        assertEquals(testLocale, era.locale());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEraLocaleOfThrowsIfNoLocaleData() {
        EraLocale.of(0, Locale.of("zz")); // Assuming "zz" is not a valid locale
    }

    @Test
    public void testEraLocaleOfReturnsEmptyStringsForInvalidEra() {
        EraLocale era = EraLocale.of(1, testLocale); // 1 is not present in eraNames, eraAbbr, eraNarrow
        assertEquals(1, era.value());
        assertEquals("", era.name());
        assertEquals("", era.abbreviation());
        assertEquals("", era.narrow());
        assertEquals(testLocale, era.locale());
    }

    // Helper to access private static method
    static class EraLocaleTestHelper {
        private EraLocaleTestHelper() {}
        public static JsonNode traverseToEraProxy(JsonNode node, String code) {
            return EraLocale.traverseToEra(node, code);
        }
    }
}