package xyz.khooz.chrono.persian.cldr;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CldrLocale {

    private CldrLocale() {
        // Private constructor to prevent instantiation
    }

    // Set of available CLDR locales (this should be dynamically loaded from CLDR data)
    private static final Set<String> CLDR_LOCALES;

    // Map of known fallbacks (if needed for specific cases)
    private static final Map<String, String> REGION_FALLBACKS = Map.of(
        "fa-IR", "fa",  // Persian (Iran) falls back to generic Persian
        "ar-EG", "ar",  // Arabic (Egypt) falls back to generic Arabic
        "fr-CA", "fr"   // French (Canada) falls back to generic French
    );

    static {
        // Load CLDR locales dynamically if needed
        // This could be from a file, database, or other source
        // For simplicity, we use a static set here
        CLDR_LOCALES = getDirectoryNames(String.format(DataCache.BASE_PATH, "", "").replace("//", ""));
    }

    /**
     * Retrieves the names of all directories in the specified path.
     * If the path starts with "$RESOURCES/", it resolves the path relative to the resources directory.
     *
     * @param path The path to the directory.
     * @return A set of directory names.
     */
    public static Set<String> getDirectoryNames(String path) {
        Set<String> directories = new HashSet<>();
        Path resolvedPath = Paths.get(path);
        File folder = resolvedPath.toFile();

        if (!folder.exists()) {
            String basePath = System.getProperty("resource.path", "src/main/resources/");  // Default if not set
            resolvedPath = Paths.get(basePath, path).normalize();
        }

        folder = new File(resolvedPath.toString());

        if (!folder.exists()) {
            throw new IllegalArgumentException("The specified path does not exist: " + resolvedPath);
        }
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("The specified path is not a directory: " + resolvedPath);
        }
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    directories.add(file.getName());
                }
            }
        }

        return directories;
    }

    /**
     * Finds the best matching CLDR locale for a given Java Locale.
     *
     * @param locale The Java Locale object.
     * @return The closest matching CLDR locale string.
     */
    public static String getCldrLocale(Locale locale) {
        String languageTag = locale.toLanguageTag(); // e.g., "fa-IR"
        String language = locale.getLanguage();      // e.g., "fa"

        // 1. Exact match
        if (CLDR_LOCALES.contains(languageTag)) {
            return languageTag;
        }

        // 2. Try language-only version
        if (CLDR_LOCALES.contains(language)) {
            return language;
        }

        // 3. Check known fallbacks
        if (REGION_FALLBACKS.containsKey(languageTag)) {
            return REGION_FALLBACKS.get(languageTag);
        }

        

        // 4. Try a related region (e.g., "fa-AF" for "fa-IR")
        for (String cldrLocale : CLDR_LOCALES) {
            if (cldrLocale.startsWith(language)) {
                return cldrLocale;
            }
        }

        // 5. Default to English
        return "en";
    }

}