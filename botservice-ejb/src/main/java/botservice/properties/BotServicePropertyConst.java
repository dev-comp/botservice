package botservice.properties;

/**
 * Константы пропертей приложения
 */
public interface BotServicePropertyConst {
    // JNDI-имя Bundle контекста
    String OSGI_BUNDLE_CONTEXT_JNDI_NAME = "java:jboss/osgi/BundleContext";
    // Имя файла параметров
    String PROPERTIES_FILE_PATH = "botservice.properties";
    // Имя параметра из файла параметров, задающего путь сохранения jar-ников адаптеров
    String ADAPTER_FILE_PATH = "botservice.adapter.filepath";
    // Хост сервера RabbitMQ
    String RABBITMQ_HOST = "rabbitmq.host";
    // Порт сервера RabbitMQ
    String RABBITMQ_PORT = "rabbitmq.port";
    // Имя прослушиваемой очереди для служебных сообщений
    String MANAGEMENT_QUEUE_NAME = "management.queue.name";
    // Имя прослушиваемой очереди для сообщений от экземпляров ботов
    String BOT_QUEUE_NAME = "bot.queue.name";
}
