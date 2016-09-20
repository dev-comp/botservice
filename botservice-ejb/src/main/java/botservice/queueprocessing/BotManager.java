package botservice.queueprocessing;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotAdapterEntity_;
import botservice.model.bot.BotEntryEntity;
import botservice.model.bot.BotEntryEntity_;
import botservice.model.system.UserKeyEntity;
import botservice.model.system.UserKeyEntity_;
import botservice.model.system.UserLogEntity;
import botservice.properties.BotServiceProperty;
import botservice.properties.BotServicePropertyConst;
import botservice.service.BotService;
import botservice.service.common.BaseParam;
import botservice.service.common.BaseService;
import botservice.util.BotMsgDirectionType;
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
import javax.inject.Inject;
import java.io.IOException;
import java.util.Date;
import java.util.List;

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
  @BotServiceProperty(name = BotServicePropertyConst.ENTRY_QUEUE_NAME)
  private String entryQueueName;

  @Inject
  BotService botService;

  @Inject
  BotManagerService botManagerService;

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

      final String fullEntryQueueName = IBotConst.QUEUE_SERVICE_PREFIX + entryQueueName;
      channel.queueDeclare(fullEntryQueueName, false, false, false, null);
      channel.basicConsume(fullEntryQueueName, true, new EntryQueueConsumer(channel));
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

  public class ManagementQueueConsumer extends CommonQueueConsumer {

    public ManagementQueueConsumer(Channel channel) {
      super(channel);
    }

    @SuppressWarnings("unused")
    IQueueConsumer adapterProcessMessageConsumer = new AbstractQueueConsumer(BotCommand.SERVICE_GET_ACTIVE_ENTRIES) {

      @Override
      public void handleMessage(Message message) throws IOException {
        String adapterName = message.getServiceProperties().get(IBotConst.PROP_ADAPTER_NAME);
        if (adapterName == null)
          throw new RuntimeException("Unknown adapter");
        BotAdapterEntity botAdapterEntity =
                botService.getEntityByCriteria(BotAdapterEntity.class, new BaseParam(BotAdapterEntity_.name, adapterName));
        if (botAdapterEntity == null)
          throw new RuntimeException("Adapter not found");
        for(BotEntryEntity botEntryEntity: botService.getActiveAdapterEntriesList(botAdapterEntity))
          botManagerService.stopEntrySession(botEntryEntity);
      }
    };
  }

  public class EntryQueueConsumer extends CommonQueueConsumer {

    public EntryQueueConsumer(Channel channel) {
      super(channel);
    }

    @SuppressWarnings("unused")
    IQueueConsumer entryProcessMessageConsumer = new AbstractQueueConsumer(BotCommand.SERVICE_PROCESS_ENTRY_MESSAGE) {

      @Override
      public void handleMessage(Message message) throws IOException {
        String msgBody = message.getUserProperties().get(IBotConst.PROP_BODY_TEXT);
        // Ищем экземпляр бота
        BotEntryEntity botEntryEntity = botService.getEntityByCriteria(BotEntryEntity.class,
                new BaseParam(BotEntryEntity_.name, message.getServiceProperties().get(IBotConst.PROP_ENTRY_NAME)));
        // записываем в таблицу юзеров
        String userName = message.getServiceProperties().get(IBotConst.PROP_USER_NAME);
        List<UserKeyEntity> userKeyEntitiesList = botService.getEntityListByCriteria(UserKeyEntity.class,
                new BaseParam(UserKeyEntity_.userName, userName),
                new BaseParam(UserKeyEntity_.botEntryEntity, botEntryEntity));
        UserKeyEntity userKeyEntity;
        if (userKeyEntitiesList.size() > 0)
          userKeyEntity = userKeyEntitiesList.get(0);
        else
          userKeyEntity = new UserKeyEntity();
        userKeyEntity.setBotEntryEntity(botEntryEntity);
        userKeyEntity.setUserName(userName);
        userKeyEntity = botService.mergeEntity(userKeyEntity);
        // Записываем в лог
        UserLogEntity userLogEntity = new UserLogEntity();
        userLogEntity.setDirectionType(BotMsgDirectionType.IN);
        userLogEntity.setMsgBody(msgBody);
        userLogEntity.setMsgTime(new Date(System.currentTimeMillis()));
        userLogEntity.setUserKeyEntity(userKeyEntity);
        botService.mergeEntity(userLogEntity);
        // пробрасываем клиенту
        //todo реализация метода проброски сообщений клиентским приложениям
        System.err.println(msgBody);
      }
    };
  }

}
