package botservice.properties;

import botservice.serviceException.ServiceExceptionObject;
import botservice.serviceException.ServiceException;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

/**
 * Продюсер свойств приложения
 */
@SuppressWarnings("unused")
public class BotservicePropertyProducer {

    @Inject
    private BotServicePropertyFileResolver fileResolver;

    @Inject
    @ServiceException
    Event<ServiceExceptionObject> serviceExceptionEvent;

    private void processServerException(String propertyName){
        String exceptionText = "Ошибка при чтении параметра приложения \"" +
                propertyName + "\" Продолжение работы невозможно";
        IllegalArgumentException e = new IllegalArgumentException(exceptionText);
        serviceExceptionEvent.fire(new ServiceExceptionObject(exceptionText, e));
    }

    @Produces
    @BotServiceProperty(name = "")
    public String getPropertyAsString(InjectionPoint injectionPoint) {
        String propertyName = injectionPoint.getAnnotated().getAnnotation(BotServiceProperty.class).name();
        String value = fileResolver.getProperties().getProperty(propertyName);
        if (value == null || propertyName.trim().length() == 0){
            processServerException(propertyName);
            throw new IllegalArgumentException();
        }
        return value;
    }

    @Produces
    @BotServiceProperty(name = "")
    public int getPropertyAsInteger(InjectionPoint injectionPoint) {
        String propertyName = injectionPoint.getAnnotated().getAnnotation(BotServiceProperty.class).name();
        String value = fileResolver.getProperties().getProperty(propertyName);
        if (value == null || propertyName.trim().length() == 0){
            processServerException(propertyName);
            throw new IllegalArgumentException();
        }

        return Integer.parseInt(value);
    }
}
