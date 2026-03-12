package ticket.booking.util;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    //This file is to load the application properties which has the path to different files(JSON). It is a similar implementation of .env files in node js.

    private static final Properties properties = new Properties();

    static {
        try (InputStream input =
                     ConfigLoader.class
                             .getClassLoader()
                             .getResourceAsStream("application.properties")) {

            properties.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration file", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

}
