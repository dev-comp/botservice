package botservice.queueprocessing;

import botservice.properties.BotServiceProperty;
import botservice.properties.BotServicePropertyConst;
import botservice.serviceException.ServiceException;
import botservice.serviceException.ServiceExceptionObject;
import com.bftcom.devcomp.api.BotCommand;
import com.bftcom.devcomp.api.IBotConst;
import com.bftcom.devcomp.api.Message;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс, инкапсулирущий методы лдя работы с очередями
 * date: 18.09.2016
 *
 * @author p.shapoval
 */

@Singleton
@Startup
public class BotManager {
  private static final Logger logger = LoggerFactory.getLogger(BotManager.class);

  private Connection connection;
  private Channel channel;

  @Inject
  @BotServiceProperty(name = BotServicePropertyConst.RABBITMQ_HOST)
  private String rabbitMQHost;

  @Inject
  @BotServiceProperty(name = BotServicePropertyConst.RABBITMQ_PORT)
  private int rabbitMQPort;

  @Inject
  @BotServiceProperty(name = BotServicePropertyConst.MANAGEMENT_QUEUE_NAME)
  private String managementQueueName;

  @Inject
  @BotMessageProcessor
  Event<Message> botMessageProcessorEvent;

  @Inject
  @ActiveBotGetter
  Event<Message> activeBotGetterEvent;

  @ServiceException
  Event<ServiceExceptionObject> serviceExceptionEvent;


  private Map<String, String> botConsumersMap = new HashMap<>();

  @PostConstruct
  public void init() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(rabbitMQHost);
    factory.setPort(rabbitMQPort);
    try {
      connection = factory.newConnection();
      channel = connection.createChannel();
      final String fullManagementQueueName = IBotConst.QUEUE_TO_SERVICE_PREFIX + managementQueueName;
      channel.queueDeclare(fullManagementQueueName, false, false, false, null);
      channel.basicConsume(fullManagementQueueName, true, new ManagementQueueConsumer(channel));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Channel getChannel() {
    return channel;
  }

  @PreDestroy
  public void close() {
    try {
      channel.close();
      connection.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void registerBotQueueConsumer(String uqBotName) {
    final String fullBotQueueName = IBotConst.QUEUE_FROM_BOT_PREFIX + uqBotName;
    try {
      if (channel.isOpen()) {
        channel.queueDeclare(fullBotQueueName, false, false, false, null);
        String consumerTag = channel.basicConsume(fullBotQueueName, true, new BotQueueConsumer(channel));
        botConsumersMap.put(uqBotName, consumerTag);
      } else {
        logger.error("!!! Attention !!!");
        logger.error("The communication channel is closed. DEBUG THIS CASE");
      }
    } catch (AlreadyClosedException | IOException e) {
      serviceExceptionEvent.fire(new ServiceExceptionObject("Ошибка при попытке подписаться на очередь " + fullBotQueueName, e));
      throw new RuntimeException(e);
    }
  }

  public void unRegisterBotQueueConsumer(String uqBotName) {
    try {
      if (channel.isOpen()) {
        channel.basicCancel(botConsumersMap.get(uqBotName));
      } else {
        logger.error("!!! Attention !!!");
        logger.error("The communication channel is closed. DEBUG THIS CASE");
      }
    } catch (AlreadyClosedException | IOException e) {
      serviceExceptionEvent.fire(new ServiceExceptionObject("Ошибка при попытке отписаться от очереди " + IBotConst.QUEUE_FROM_BOT_PREFIX + uqBotName, e));
    }
  }

  private class ManagementQueueConsumer extends CommonQueueConsumer {

    public ManagementQueueConsumer(Channel channel) {
      super(channel);
    }

    @SuppressWarnings("unused")
    IQueueConsumer adapterProcessMessageConsumer = new AbstractQueueConsumer(BotCommand.SERVICE_GET_ACTIVE_BOTS) {

      @Override
      public void handleMessage(Message message) throws IOException {
        activeBotGetterEvent.fire(message);
      }
    };
  }

  private class BotQueueConsumer extends CommonQueueConsumer {

    public BotQueueConsumer(Channel channel) {
      super(channel);
    }

    @SuppressWarnings("unused")
    IQueueConsumer botProcessMessageConsumer = new AbstractQueueConsumer(BotCommand.SERVICE_PROCESS_BOT_MESSAGE) {

      @Override
      public void handleMessage(Message message) throws IOException {
        botMessageProcessorEvent.fire(message);
      }
    };
  }

}
