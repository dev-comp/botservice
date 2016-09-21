package botservice.queueprocessing;

import botservice.model.bot.BotEntity;
import botservice.model.bot.BotEntity_;
import botservice.model.client.ClientAppEntity;
import botservice.model.client.ClientAppEntity_;
import botservice.model.system.UserKeyEntity;
import botservice.model.system.UserKeyEntity_;
import botservice.model.system.UserLogEntity;
import botservice.rest.model.MsgObject;
import botservice.rest.model.UserObject;
import botservice.service.BotService;
import botservice.service.ClientService;
import botservice.service.common.BaseParam;
import botservice.util.BotMsgDirectionType;
import botservice.serviceException.ServiceExceptionObject;
import com.bftcom.devcomp.bots.IBotConst;
import com.bftcom.devcomp.bots.Message;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import botservice.serviceException.ServiceException;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

/**
 * Класс-обработчик входящих сообщений
 */

@Stateless
public class BotMessageProcessorHandler {

    @Inject
    BotService botService;

    @Inject
    ClientService clientService;

    @Inject
    BotManagerService botManagerService;

    @Inject
    @ServiceException
    Event<ServiceExceptionObject> serviceExceptionEvent;

    /**
     * Запись системной информации
     * @param message - входящее сообщение
     */
    public void handleMessage(@Observes @BotMessageProcessor Message message){
        try {
            BotEntity botEntity = botService.getEntityByCriteria(BotEntity.class,
                    new BaseParam(BotEntity_.name, message.getServiceProperties().get(IBotConst.PROP_BOT_NAME)));
            doSystemActions(message, botEntity);
            doBusinessActions(message, botEntity);
        } catch (Exception e){
            serviceExceptionEvent.fire(new ServiceExceptionObject("Ошибка при обработке сообщения от бота", e));
        }
    }

    private void doSystemActions(Message message, BotEntity botEntity){
        // записываем в таблицу юзеров
        String userName = message.getServiceProperties().get(IBotConst.PROP_USER_NAME);
        List<UserKeyEntity> userKeyEntitiesList = botService.getEntityListByCriteria(UserKeyEntity.class,
                new BaseParam(UserKeyEntity_.userName, userName),
                new BaseParam(UserKeyEntity_.botEntity, botEntity));
        UserKeyEntity userKeyEntity;
        if (userKeyEntitiesList.size() > 0)
            userKeyEntity = userKeyEntitiesList.get(0);
        else
            userKeyEntity = new UserKeyEntity();
        userKeyEntity.setBotEntity(botEntity);
        userKeyEntity.setUserName(userName);
        userKeyEntity.setProps(message.getServiceProperties());
        userKeyEntity = botService.mergeEntity(userKeyEntity);
        // Записываем в лог
        UserLogEntity userLogEntity = new UserLogEntity();
        userLogEntity.setDirectionType(BotMsgDirectionType.IN);
        userLogEntity.setMsgBody(message.getUserProperties().get(IBotConst.PROP_BODY_TEXT));
        userLogEntity.setMsgTime(new Date(System.currentTimeMillis()));
        userLogEntity.setUserKeyEntity(userKeyEntity);
        botService.mergeEntity(userLogEntity);
    }

    private void doBusinessActions(Message message, BotEntity botEntity){
        ClientAppEntity clientAppEntity = clientService.getEntityByCriteria(ClientAppEntity.class,
                new BaseParam(ClientAppEntity_.botEntity, botEntity));
        UserObject userObject = new UserObject();
        userObject.setBotName(botEntity.getName());
        userObject.setUserName(message.getServiceProperties().get(IBotConst.PROP_USER_NAME));
        MsgObject requestMsgObject = new MsgObject();
        requestMsgObject.setUserObject(userObject);
        requestMsgObject.setMsgBody(message.getUserProperties().get(IBotConst.PROP_BODY_TEXT));
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(clientAppEntity.getPath());
        Response response = target.request().post(Entity.entity(requestMsgObject, MediaType.APPLICATION_JSON));
        MsgObject responseMsgObject = response.readEntity(MsgObject.class);
        botManagerService.sendMessageToBot(responseMsgObject);
        response.close();
    }
}
