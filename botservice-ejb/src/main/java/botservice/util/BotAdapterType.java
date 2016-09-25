package botservice.util;

/**
 * Тип адаптера
 */
public enum BotAdapterType {
    OSGI_TYPE, STANDALONE_TYPE;

    public String getName(){
        return name();
    }
}
