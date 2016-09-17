package botservice.properties;

/**
 * Константы пропертей приложения
 */
public interface BotServicePropertyConst {
    // JNDI-имя Bundle контекста
    String OSGI_BUNDLE_CONTEXT_JNDI_NAME = "java:jboss/osgi/BundleContext";
    // MappedName для Message Driven Bean обрабтчика сообщений от экземпляров ботов
    String JMS_BOTENTRY_QUEUE_MAPPED_NAME = "jms/queue/BotEntryQueue";
    // JNDI-имя очереди для входящи сообщениий от экземпляров ботов
    String JMS_BOTENTRY_DESTINATION_JNDI_NAME = "java:/jms/queue/BotEntryQueue";
    // MappedName для Message Driven Bean обрабтчика сообщений от адаптеров ботов
    String JMS_BOTADAPTER_QUEUE_MAPPED_NAME = "jms/queue/BotAdapterQueue";
    // JNDI-имя очереди для входящи сообщениий от адаптеров ботов
    String JMS_BOTADAPTER_DESTINATION_JNDI_NAME = "java:/jms/queue/BotAdapterQueue";

    // Имя файла параметров
    String PROPERTIES_FILE_PATH = "botservice.properties";
    // Имя параметра из файла параметров, задающего путь сохранения jar-ников адаптеров
    String ADAPTER_FILE_PATH = "botservice.adapter.filepath";
}
