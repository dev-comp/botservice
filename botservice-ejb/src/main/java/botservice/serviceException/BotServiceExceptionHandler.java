package botservice.serviceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;

/**
 * Обработчик ошибок
 */

@Stateless
public class BotServiceExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(BotServiceExceptionHandler.class);

    public void handleError(@Observes @ServiceException ServiceExceptionObject serviceExceptionObject){
        logger.error(serviceExceptionObject.getMessage(), serviceExceptionObject.getThrowable());
        throw new RuntimeException(serviceExceptionObject.getThrowable());
    }
}
