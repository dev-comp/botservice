package botservice.properties;

/**
 * Константы пропертей приложения
 */
public interface BotServicePropertyConst {
    // Пока просто объявляем константу. При необходимости вынесем в настройки
    String OSGI_BUNDLE_CONTEXT_JNDI_NAME = "java:jboss/osgi/BundleContext";

    String PROPERTIES_FILE_PATH = "botservice.properties";

    String ADAPTER_FILE_PATH = "botservice.adapter.filepath";
}
