package botservice.mdb;

import botservice.properties.BotServicePropertyConst;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Бин для обработки сервисных запросов от адаптеров ботов
 */

@MessageDriven(mappedName = BotServicePropertyConst.JMS_BOTADAPTER_QUEUE_MAPPED_NAME,
        activationConfig = {@ActivationConfigProperty(propertyName = "destination",
                propertyValue = BotServicePropertyConst.JMS_BOTADAPTER_DESTINATION_JNDI_NAME)})
public class BotAdapterMDB implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            System.err.println("BotAdapterServiceMDB: " + message.getBody(String.class));
            //todo Реализовать разбор сообщения от адаптера бота и ответить в зависимости от запроса
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

}
