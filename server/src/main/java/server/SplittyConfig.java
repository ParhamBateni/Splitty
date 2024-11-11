package server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class SplittyConfig {
    private final String CONFIG_FILE_PATH = "splitty-config.properties";

    @Bean
    public String getServerUrl() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().
                getResourceAsStream(CONFIG_FILE_PATH)) {
            if (inputStream != null) {
                Properties properties = new Properties();
                properties.load(inputStream);
                return properties.getProperty("server.url");
            } else {
                throw new IOException("Unable to load configuration file: " + CONFIG_FILE_PATH);
            }
        }
    }
}
