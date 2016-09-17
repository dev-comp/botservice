package botservice.mdb;

import botservice.properties.BotServicePropertyConst;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Класс, обрабатывающий сообщения, поступающие от ботов
 */

@MessageDriven(mappedName = BotServicePropertyConst.JMS_BOTENTRY_QUEUE_MAPPED_NAME,
        activationConfig = {@ActivationConfigProperty(propertyName = "destination",
                propertyValue = BotServicePropertyConst.JMS_BOTENTRY_DESTINATION_JNDI_NAME)})
public class BotEntryMDB implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            System.err.println("BotEntryUpdateReceivedMDB: " + message.getBody(String.class));
            //todo Реализовать разбор сообщения от экземпляра бота и пробросить нужному клиентскому приложению
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
