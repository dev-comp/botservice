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
}
