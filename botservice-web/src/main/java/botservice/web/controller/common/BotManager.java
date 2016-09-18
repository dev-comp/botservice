package botservice.web.controller.common;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotEntryEntity;
import botservice.properties.BotServiceProperty;
import botservice.properties.BotServicePropertyConst;
import com.bftcom.devcomp.bots.Commands;
import com.bftcom.devcomp.bots.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Класс, инкапсулирущий методы лдя работы с очередями
 * date: 18.09.2016
 *
 * @author p.shapoval
 */
@ApplicationScoped
public class BotManager {
  private static ObjectMapper mapper = new ObjectMapper();
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
  private String entryQueueNAme;
  
  @PostConstruct
  public void init () {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(rabbitMQHost);
    factory.setPort(rabbitMQPort);
    try {
      connection = factory.newConnection();
      channel = connection.createChannel();
      channel.queueDeclare(managementQueueName, false, false, false, null);// todo нужен слушатель
      channel.queueDeclare(entryQueueNAme, false, false, false, null);     // todo нужен слушатель
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean startEntrySession(BotEntryEntity botEntryEntity) {
    Message message = new Message();
    message.setCommand(Commands.START_ENTRY.name());
    Map<String, String> propMap = botEntryEntity.getBotAdapterEntity().getProps();
    propMap.putAll(botEntryEntity.getProps());
    message.setProperties(propMap);
    try {
      channel.basicPublish("", botEntryEntity.getBotAdapterEntity().getName(), null,
              mapper.writeValueAsString(message).getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return true;
  }

  public boolean stopEntrySession(BotEntryEntity botEntryEntity) {
    try {
      Message message = new Message();
      message.setCommand(Commands.STOP_ENTRY.name());
      Map<String, String> propMap = botEntryEntity.getBotAdapterEntity().getProps();
      propMap.putAll(botEntryEntity.getProps());
      message.setProperties(propMap);
      channel.basicPublish("", botEntryEntity.getBotAdapterEntity().getName(), null,
              mapper.writeValueAsString(message).getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return true;
  }

  public boolean stopAllEntries(BotAdapterEntity botAdapterEntity){
    //todo послать команду на остановку всех ботов конкретного адаптера
    return true;
  }

  @PreDestroy
  public void close() {
    try {
      channel.close();
      connection.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
