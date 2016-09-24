package botservice.service;

import botservice.model.bot.BotEntity;
import botservice.model.system.UserKeyEntity;
import botservice.queueprocessing.BotManager;
import botservice.queueprocessing.IQueueConsumer;
import botservice.rest.model.MsgObject;
import botservice.rest.model.UserObject;
import botservice.service.common.BaseService;
import botservice.util.BotMsgDirectionType;
import com.bftcom.devcomp.api.BotCommand;
import com.bftcom.devcomp.api.IBotConst;
import com.bftcom.devcomp.api.Message;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Методы для работы с внутренними сущностями сервиса
 */

@Stateless
public class SystemService extends BaseService {

    @Inject
    BotManager botManager;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public MsgObject sendMsgToClntApp(String userName, String bodyText, BotEntity botEntity){
        UserObject userObject = new UserObject();
        userObject.setBotName(botEntity.getName());
        userObject.setUserName(userName);
        MsgObject requestMsgObject = new MsgObject();
        requestMsgObject.setUserObject(userObject);
        requestMsgObject.setDirectionType(BotMsgDirectionType.TO_CLIENT_APP.name());
        requestMsgObject.setMsgBody(bodyText);
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(botEntity.getClientAppEntity().getPath());
        Response response = target.request().post(Entity.entity(requestMsgObject, MediaType.APPLICATION_JSON));
        return response.readEntity(MsgObject.class);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void sendMessageToBotQueue(String bodyText, UserKeyEntity userKeyEntity) throws IOException {
        Message message = new Message();
        message.setCommand(BotCommand.SERVICE_PROCESS_BOT_MESSAGE);
        message.setServiceProperties(userKeyEntity.getProps());
        message.getUserProperties().put(IBotConst.PROP_BODY_TEXT, bodyText);
        String queueName = IBotConst.QUEUE_TO_BOT_PREFIX + userKeyEntity.getBotEntity().getName();
        botManager.getChannel().queueDeclare(queueName, false, false, false, null);
        botManager.getChannel().basicPublish("", queueName, null, IQueueConsumer.mapper.writeValueAsString(message).getBytes(StandardCharsets.UTF_8));
    }

}
