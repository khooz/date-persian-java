package xyz.khooz.chrono.persian.cldr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private ConfigLoader() {
        // Private constructor to prevent instantiation
    }

    public static String getConfig(String key, String defaultValue, String path) throws IOException, IllegalArgumentException {
        // Validate the key
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty");
        }
        // 1. System property
        String value = System.getProperty(key);
        if (value != null) return value;

        // 2. Environment variable (convert dots to underscores and uppercase)
        String envKey = key.replace('.', '_').toUpperCase();
        value = System.getenv(envKey);
        if (value != null) {
            System.setProperty(key, value); // Set system property for consistency
            return value;
        }

        if (path != null && !path.isEmpty()) {
            // 3. Properties file (cldr.properties in classpath root)
            try (InputStream in = ConfigLoader.class.getResourceAsStream(path)) {
                if (in != null) {
                    Properties props = new Properties();
                    props.load(in);
                    value = props.getProperty(key);
                    if (value != null) {
                        System.setProperty(key, value); // Set system property for consistency
                        return value;
                    }
                }
            }
        }
        
        // 4. Default
        System.setProperty(key, defaultValue); // Set system property for consistency
        return defaultValue;
    }

    public static String getConfig(String key, String defaultValue) throws IOException, IllegalArgumentException {
        return getConfig(key, defaultValue, null);
    }
}
