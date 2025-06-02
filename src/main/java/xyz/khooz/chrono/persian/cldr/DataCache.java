package xyz.khooz.chrono.persian.cldr;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;

public class DataCache {

    public static class Props {
        private Props() {
            // Private constructor to prevent instantiation
        }
        public static final String NAMESPACE = "cldr";
        public static final String BASE_PATH = "base.path";
        public static final String DEFAULT_PATH = "/cldr-cal-persian-full/main";
        public static final String PROPERTIES_FILE = String.format("/%s.properties", NAMESPACE);
        public static final String FILE_NAME = "ca-persian.json";
    }

    DataCache() {
        // Private constructor to prevent instantiation
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // Cache for storing loaded CLDR data by locale code.
    private static final Map<String, JsonNode> LOCALE_CACHE = new ConcurrentHashMap<>();

    // Define popular locales to preload.
    private static final List<String> POPULAR_LOCALES = List.of("fa", "en");

    public static final DataCache INSTANCE = new DataCache();
    /**
     * The base resource path pattern. Note that by default we look into the classpath,
     * but this can be made configurable via a system property (or other mechanism)
     * so that the JSON files can be provided externally.
     *
     * For example, if the CLDR JSON jar/resource is external, you could set:
     *   -Dcldr.base.path=/external/cldr/ca-persian-%s.json
     */
    public static final String BASE_PATH;

    // Static initialization: preload popular locales.
    static {
        try  {
            BASE_PATH = ConfigLoader.getConfig(Props.NAMESPACE + "." + Props.BASE_PATH, Props.DEFAULT_PATH, Props.PROPERTIES_FILE);
        } catch (IOException e) {
            throw new CldrDataLoadException("Failed to load configuration properties", e);
        }
        for (String locale : POPULAR_LOCALES) {
            loadLocale(locale);
        }
    }

    /**
     * Returns the parsed JSON node for the given locale.
     * If the locale’s data is not already loaded, this method loads it dynamically, caches it,
     * and then returns it.
     *
     * @param locale The locale code (e.g., "fa" or "en").
     * @return The JSON node corresponding to the CLDR data for that locale.
     * @throws RuntimeException if the resource cannot be loaded.
     */
    public static JsonNode getLocaleData(Locale locale) {
        return getLocaleData(CldrLocale.getCldrLocale(locale));
    }

    /**
     * Returns the parsed JSON node for the given locale.
     * If the locale’s data is not already loaded, this method loads it dynamically, caches it,
     * and then returns it.
     *
     * @param locale The locale code (e.g., "fa" or "en").
     * @return The JSON node corresponding to the CLDR data for that locale.
     * @throws RuntimeException if the resource cannot be loaded.
     */
    public static JsonNode getLocaleData(String locale) {
        return LOCALE_CACHE.computeIfAbsent(locale, DataCache::loadLocale);
    }

    /**
     * Loads the CLDR JSON data for the given locale from the configured resource path.
     *
     * @param locale The locale code.
     * @return The JSON node loaded.
     */
    private static JsonNode loadLocale(String locale) {
        String path = String.format(BASE_PATH, locale, Props.FILE_NAME);
        JsonNode json;
        try (InputStream in = DataCache.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("CLDR data resource not found for locale: " + locale);
            }
            json = MAPPER.readTree(in);
            
        } catch (IOException ex) {
            throw new CldrDataLoadException("Failed to load CLDR data for locale: " + locale, ex);
        }
        // Optionally verify the JSON is structured as expected.
        try (InputStream schemaStream = DataCache.class.getResourceAsStream("/ca-persian-schema.json")) {
            if (schemaStream == null) {
                throw new IllegalStateException("JSON schema resource not found: /ca-persian-schema.json");
            }
            JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(com.networknt.schema.SpecVersion.VersionFlag.V7);
            JsonSchema schema = schemaFactory.getSchema(schemaStream);
            java.util.Set<ValidationMessage> errors = schema.validate(json);
            if (!errors.isEmpty()) {
                throw new IllegalArgumentException("CLDR data for locale " + locale + " does not match schema: " + errors);
            }
        } catch (IOException ex) {
            throw new CldrDataLoadException("Failed to load CLDR data for locale: " + locale, ex);
        }
        return json;
    }

    public static class CldrDataLoadException extends RuntimeException {
        public CldrDataLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}