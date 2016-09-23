package botservice.properties;

import botservice.serviceException.ServiceExceptionObject;
import botservice.serviceException.ServiceException;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
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

    @Inject
    @ServiceException
    Event<ServiceExceptionObject> serviceExceptionEvent;

    @PostConstruct
    private void init() {
        try {
            String propertyFile = System.getProperty(BotServicePropertyConst.PROPERTIES_FILE_PATH);
            File file = new File("D:\\git\\github\\dev-comp\\botservice\\botservice-ejb\\target\\classes\\botservice.properties");
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            serviceExceptionEvent.fire(new ServiceExceptionObject(
                    "Ошибка при чтении файла настроек, указанного в параметре botservice.properties конфигурационного файла сервера. " +
                            "Продолжение работы невозможно", e));
            throw new IllegalArgumentException(e);
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
