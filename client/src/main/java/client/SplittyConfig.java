package client;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Properties;

@Component
public  class SplittyConfig {
    private final String CONFIG_FILE_PATH = SplittyConfig.class.getClassLoader()
            .getResource("client.properties").getFile().replace("main", "")
            .replace("build", "src/main").replace("%20", " ");
    private final int MAX_RECENT_EVENTS = 8;

    private static Properties properties;

    public SplittyConfig() {
        loadProperties();
    }

    private void loadProperties() {
        properties = new Properties();
        try {
            FileReader fr = new FileReader(CONFIG_FILE_PATH);
            properties.load(fr);
            fr.close();
        } catch (IOException e) {
            new IOException("Unable to load configuration file: " +
                    CONFIG_FILE_PATH).printStackTrace();
        }
    }

    private Object getProperty(String key) {
        if (properties != null) {
            return properties.getProperty(key);
        }
        return null;
    }

    private static Object getStaticProperty (String key) {
        if (properties != null) {
            return properties.getProperty(key);
        }
        return null;
    }

    private void saveProperty(String key, String value) {
        if (properties != null) {
            properties.setProperty(key, value);
        }
    }

    public void writeProperties() {
        try {
            FileWriter fileWriter = new FileWriter(CONFIG_FILE_PATH);
            properties.store(fileWriter, "");
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            new IOException("Unable to write in configuration file: " +
                    CONFIG_FILE_PATH).printStackTrace();
        }
    }

    public static String getServerUrl() {
        return (String) getStaticProperty("server.url");
    }

    public Language getPreferredLanguage() {
        return new Language((String) getProperty("preferred.language"));
    }

    public void setPreferredLanguage(String language) {
        if (language.equals("EN") || language.equals("NL") || language.equals("DE")) {
            saveProperty("preferred.language", language);
        }
    }

    public List<Long> getRecentEventIds() {
        List<Long> eventIds = new ArrayList<>();
        for (int i = 1; i <= MAX_RECENT_EVENTS; i++) {
            String eventIdStr = (String) getProperty("recent_event_id_" + i);
            if (eventIdStr != null && !eventIdStr.equals("") &&
                    Integer.parseInt(eventIdStr) != -1) {
                System.out.println(eventIdStr);
                eventIds.add(Long.parseLong(eventIdStr));
            }
        }
        return eventIds;
    }

    public void setRecentEventId(int index, long eventId) {
        saveProperty("recent_event_id_" + index, String.valueOf(eventId));
    }

    public void resetRecentEventIds() {
        URL configFileUrl = SplittyConfig.class.getClassLoader().getResource(CONFIG_FILE_PATH);
        Properties properties = new Properties();
        try (InputStream inputStream = SplittyConfig.class.
                getClassLoader().getResourceAsStream(CONFIG_FILE_PATH)) {
            if (inputStream != null) {
                properties.load(inputStream);
                for (int i = 1; i <= MAX_RECENT_EVENTS; i++) {
                    properties.setProperty("recent_event_id_" + i, "-1");
                }
                try (OutputStream outputStream =
                             new FileOutputStream(new File(configFileUrl.toURI()))) {
                    properties.store(outputStream, "Store recent event");
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                throw new FileNotFoundException("Configuration file not found: "
                        + CONFIG_FILE_PATH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Currency getPrefferedCurrency() {
        String currency = (String) getProperty("preferred.currency");
        if (!currency.equals("USD") && !currency.equals("EUR") && !currency.equals("CHF")) {
            new IllegalArgumentException("Unsupported currency is set in config file!" +
                    " Change it to USD, EUR, or CHF!").printStackTrace();
            return Currency.getInstance("EUR");
        } else {
            return Currency.getInstance(currency);
        }
    }

    public void setPreferredCurrency(String currency) {
        saveProperty("preferred.currency", currency);
    }

    public String getEmail() {
        return (String) getProperty("email.username");
    }

    public String getPassword() {
        return (String) getProperty("email.password");
    }

    public void setEmail(String email) {
        saveProperty("email.username", email);
    }

    public void setPassword(String password) {
        saveProperty("email.password", password);
    }
}






