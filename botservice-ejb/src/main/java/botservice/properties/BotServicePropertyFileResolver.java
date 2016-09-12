package botservice.properties;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton, отвечающий за парсинг параметров
 */

@Singleton
public class BotServicePropertyFileResolver {

    private Properties properties = new Properties();

    @PostConstruct
    private void init() {
        String propertyFile = System.getProperty("botservice.properties");
        File file = new File(propertyFile);
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            System.out.println("Unable to load properties file" + e);
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
