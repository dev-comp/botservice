package botservice.service;

import botservice.model.bot.BotEntity;
import botservice.rest.model.MsgObject;
import botservice.rest.model.UserObject;
import botservice.service.common.BaseService;
import botservice.util.BotMsgDirectionType;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Методы для работы с внутренними сущностями сервиса
 */

@Stateless
public class SystemService extends BaseService {

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
}
