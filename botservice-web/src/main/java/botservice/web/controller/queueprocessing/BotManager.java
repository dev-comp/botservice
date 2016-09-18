package botservice.web.controller.queueprocessing;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotEntryEntity;
import botservice.properties.BotServiceProperty;
import botservice.properties.BotServicePropertyConst;
import com.bftcom.devcomp.bots.*;
import com.bftcom.devcomp.bots.BotCommand;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Класс, инкапсулирущий методы лдя работы с очередями
 * date: 18.09.2016
 *
 * @author p.shapoval
 */
@ApplicationScoped
public class BotManager {

  public static ObjectMapper mapper = new ObjectMapper();

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

      final String fullManagementQueueName = BotConst.QUEUE_SERVICE_PREFIX + managementQueueName;
      channel.queueDeclare(fullManagementQueueName, false, false, false, null);
      channel.basicConsume(fullManagementQueueName, true, new ManagementQueueConsumer(channel));

      final String fullEntryQueueName = BotConst.QUEUE_SERVICE_PREFIX + entryQueueNAme;
      channel.queueDeclare(fullEntryQueueName, false, false, false, null);
      channel.basicConsume(fullEntryQueueName, true, new EntryQueueConsumer(channel));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean sendCommandToAdapter(BotCommand botCommand, BotEntryEntity botEntryEntity){
    Message message = new Message();
    message.setCommand(botCommand.name());
    Map<String, String> propMap = botEntryEntity.getBotAdapterEntity().getProps();
    propMap.putAll(botEntryEntity.getProps());
    message.setUserProperties(propMap);
    Map<String, String> serviceProperties = new HashMap<>();
    serviceProperties.put(BotConst.PROP_ENTRY_NAME, botEntryEntity.getName());
    message.setServiceProperties(serviceProperties);
    return sendCommandToBotAdapter(message, botEntryEntity.getBotAdapterEntity());
  }

  private boolean sendCommandToBotAdapter(Message message, BotAdapterEntity botAdapterEntity){
    try {
      String queueName = BotConst.QUEUE_ADAPTER_PREFIX + botAdapterEntity.getName();
      channel.queueDeclare(queueName, false, false, false, null);
      channel.basicPublish("", queueName, null,
              mapper.writeValueAsString(message).getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return true;
  }

  public boolean startEntrySession(BotEntryEntity botEntryEntity) {
    return sendCommandToAdapter(BotCommand.START_ENTRY, botEntryEntity);
  }

  public boolean stopEntrySession(BotEntryEntity botEntryEntity) {
    return sendCommandToAdapter(BotCommand.STOP_ENTRY, botEntryEntity);
  }

  public boolean stopAllEntries(BotAdapterEntity botAdapterEntity){
    Message message = new Message();
    message.setCommand(BotCommand.STOP_ALL_ENTRIES.name());
    return sendCommandToBotAdapter(message, botAdapterEntity);
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
