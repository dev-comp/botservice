package botservice.properties;

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

    @Produces
    @BotServiceProperty(name = "")
    public String getPropertyAsString(InjectionPoint injectionPoint) {
        String propertyName = injectionPoint.getAnnotated().getAnnotation(BotServiceProperty.class).name();
        String value = fileResolver.getProperties().getProperty(propertyName);
        if (value == null || propertyName.trim().length() == 0) {
            throw new IllegalArgumentException("No property found with name " + value);
        }
        return value;
    }

    @Produces
    @BotServiceProperty(name = "")
    public int getPropertyAsInteger(InjectionPoint injectionPoint) {
        String propertyName = injectionPoint.getAnnotated().getAnnotation(BotServiceProperty.class).name();
        String value = fileResolver.getProperties().getProperty(propertyName);
        if (value == null || propertyName.trim().length() == 0) {
            throw new IllegalArgumentException("No property found with name " + value);
        }
        return Integer.parseInt(value);
    }
}
