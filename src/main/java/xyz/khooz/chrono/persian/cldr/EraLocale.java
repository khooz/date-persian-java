package xyz.khooz.chrono.persian.cldr;

import java.util.Locale;

import com.fasterxml.jackson.databind.JsonNode;

public record EraLocale(int value, String name, String abbreviation, String narrow, Locale locale) {

    @SuppressWarnings("unused")
    private static final String JSON_PATH = "$.main.%s.dates.calendars.persian.eras";

    public static EraLocale of(int value, Locale locale) {
        JsonNode localeNode = DataCache.getLocaleData(locale);
        if (localeNode == null) {
            throw new IllegalArgumentException("No era data found for locale: " + locale);
        }

        // Use JSON_PATH to extract the era node for the given locale
        String localeCode = locale.toLanguageTag();
        // Use JsonPath to extract the era node as a JsonNode
        JsonNode eraNode = traverseToEra(localeNode, localeCode);
        if (eraNode.isMissingNode()) {
            throw new IllegalArgumentException("No era data found for locale: " + locale);
        }

        // Extract era details for the given value (usually 0 for Persian calendar)
        String eraKey = String.valueOf(value);
        String name = eraNode.path("eraNames").path(eraKey).asText();
        String abbreviation = eraNode.path("eraAbbr").path(eraKey).asText();
        String narrow = eraNode.path("eraNarrow").path(eraKey).asText();

        return new EraLocale(value, name, abbreviation, narrow, locale);
    }

    static JsonNode traverseToEra(JsonNode localeNode, String localeCode) {
        return localeNode
            .path("main")
            .path(localeCode)
            .path("dates")
            .path("calendars")
            .path("persian")
            .path("eras");
    }
}
