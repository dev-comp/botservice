package botservice.queueprocessing;

import botservice.properties.BotServiceProperty;
import botservice.properties.BotServicePropertyConst;
import com.bftcom.devcomp.bots.BotCommand;
import com.bftcom.devcomp.bots.IBotConst;
import com.bftcom.devcomp.bots.Message;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.IOException;

/**
 * Класс, инкапсулирущий методы лдя работы с очередями
 * date: 18.09.2016
 *
 * @author p.shapoval
 */

@Singleton
@Startup
public class BotManager {

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
  @BotServiceProperty(name = BotServicePropertyConst.BOT_QUEUE_NAME)
  private String botQueueName;

  @Inject
  @BotMessageProcessor
  Event<Message> botMessageProcessorEvent;

  @Inject
  @ActiveBotGetter
  Event<Message> activeBotGetterEvent;

  @PostConstruct
  public void init () {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(rabbitMQHost);
    factory.setPort(rabbitMQPort);
    try {
      connection = factory.newConnection();
      channel = connection.createChannel();

      final String fullManagementQueueName = IBotConst.QUEUE_SERVICE_PREFIX + managementQueueName;
      channel.queueDeclare(fullManagementQueueName, false, false, false, null);
      channel.basicConsume(fullManagementQueueName, true, new ManagementQueueConsumer(channel));

      final String fullBotQueueName = IBotConst.QUEUE_SERVICE_PREFIX + botQueueName;
      channel.queueDeclare(fullBotQueueName, false, false, false, null);
      channel.basicConsume(fullBotQueueName, true, new BotQueueConsumer(channel));
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
