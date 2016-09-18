package botservice.web.controller.common;

import com.bftcom.devcomp.bots.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * todo@shapoval add class description
 * <p>
 * date: 18.09.2016
 *
 * @author p.shapoval
 */
@ApplicationScoped
public class BotManager {
  private static ObjectMapper mapper = new ObjectMapper();
  private Connection connection;
  private Channel channel;
  
  public BotManager () {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    
    try {
      connection = factory.newConnection();
      channel = connection.createChannel();
      channel.queueDeclare(QueuesConfiguration.MANAGEMENT_QUEUE, false, false, false, null);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean startEntrySession(String uqName, Map<String, String> props) {
    // todo послать сообщение на старт бота
    props.put(Configuration.BOT_TOKEN, uqName);
    Message message = new Message();
    message.setGuid(UUID.randomUUID().toString());
    message.setType(RequestType.REQUEST.name());
    message.setCommand(Commands.START_BOT.name());
    message.setProperties(props);
    
    try {
      channel.basicPublish("", QueuesConfiguration.MANAGEMENT_QUEUE, null, mapper.writeValueAsString(message).getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return true;
  }

  public boolean stopEntrySession(String uqName) {
    //todo послать сообщение на остановку бота
    try {
      Message message = new Message();
      message.setGuid(UUID.randomUUID().toString());
      message.setType(RequestType.REQUEST.name());
      message.setCommand(Commands.STOP_BOT.name());
      
      HashMap<String, String> config = new HashMap<>();
      config.put(Configuration.PROXY_HOST, "localhost");
      config.put(Configuration.PROXY_PORT, "53128");
      config.put(Configuration.BOT_TOKEN, uqName);
      config.put(Configuration.BOT_USERNAME, "BftDevCompEchoService");
      message.setProperties(config);
      channel.basicPublish("", QueuesConfiguration.MANAGEMENT_QUEUE, null, mapper.writeValueAsString(message).getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return true;
  }

  @Override
  protected void finalize() throws Throwable {
    channel.close();
    connection.close();
    super.finalize();
  }
}
