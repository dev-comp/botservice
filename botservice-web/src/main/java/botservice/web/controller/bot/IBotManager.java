package botservice.web.controller.bot;

/**
 * Интерфейс для управления потоками адаптера (конкретными ботами)
 * @author ikka
 */
public interface IBotManager {
  boolean startBotSession(String string, String proxyHost, Integer proxyPort);

  boolean stopBotSession(String string);
}
