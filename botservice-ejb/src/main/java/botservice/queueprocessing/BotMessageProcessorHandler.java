package botservice.queueprocessing;

import botservice.model.bot.BotEntity;
import botservice.model.bot.BotEntity_;
import botservice.model.system.UserKeyEntity;
import botservice.model.system.UserKeyEntity_;
import botservice.model.system.UserLogEntity;
import botservice.rest.model.MsgObject;
import botservice.rest.model.UserObject;
import botservice.service.SystemService;
import botservice.service.common.BaseParam;
import botservice.util.BotMsgDirectionType;
import botservice.serviceException.ServiceExceptionObject;
import botservice.util.BotMsgTransportStatus;
import com.bftcom.devcomp.api.IBotConst;
import com.bftcom.devcomp.api.Message;
import botservice.serviceException.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс-обработчик входящих сообщений
 */

@Stateless
public class BotMessageProcessorHandler {
    private static final Logger logger = LoggerFactory.getLogger(BotMessageProcessorHandler.class);

    @Inject
    SystemService systemService;

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
            BotEntity botEntity = systemService.getEntityByCriteria(BotEntity.class,
                    new BaseParam(BotEntity_.name, message.getServiceProperties().get(IBotConst.PROP_BOT_NAME)));
            UserLogEntity userLogEntity = doSystemActions(message, botEntity);
            doBusinessActions(message, userLogEntity);
        } catch (Exception e){
            serviceExceptionEvent.fire(new ServiceExceptionObject("Ошибка при обработке сообщения от бота", e));
        }
    }

    private UserLogEntity doSystemActions(Message message, BotEntity botEntity){
        String userName = message.getServiceProperties().get(IBotConst.PROP_USER_NAME);
        List<UserKeyEntity> userKeyEntitiesList = systemService.getEntityListByCriteria(UserKeyEntity.class,
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
        userKeyEntity = systemService.mergeEntity(userKeyEntity);
        UserLogEntity userLogEntity = new UserLogEntity();
        userLogEntity.setDirectionType(BotMsgDirectionType.TO_CLIENT_APP);
        userLogEntity.setMsgBody(message.getUserProperties().get(IBotConst.PROP_BODY_TEXT));
        userLogEntity.setMsgTime(new Date(System.currentTimeMillis()));
        userLogEntity.setUserKeyEntity(userKeyEntity);
        return userLogEntity;
    }

    private void doBusinessActions(Message message, UserLogEntity userLogEntity){
        BotEntity botEntity = userLogEntity.getUserKeyEntity().getBotEntity();
        String userName = message.getServiceProperties().get(IBotConst.PROP_USER_NAME);
        String bodyText = message.getUserProperties().get(IBotConst.PROP_BODY_TEXT);
        Map<String, String> autoAnswersMap = new HashMap<>();
        autoAnswersMap.putAll(botEntity.getBotAdapterEntity().getAnswers());
        autoAnswersMap.putAll(botEntity.getAnswers());
        String autoAnswer = autoAnswersMap.get(bodyText);
        MsgObject responseMsgObject;
        UserObject userObject = new UserObject();
        userObject.setBotName(botEntity.getName());
        userObject.setUserName(userName);
        if (autoAnswer != null){
            responseMsgObject = new MsgObject();
            responseMsgObject.setUserObject(userObject);
            responseMsgObject.setMsgBody(autoAnswer);
        } else {
            try {
                responseMsgObject = systemService.sendMsgToClntApp(userName, bodyText, botEntity);
                userLogEntity.setTransportStatus(BotMsgTransportStatus.DELIVERED);
            } catch (Exception e) {
                logger.error("Failed to send message to client service with url: " + botEntity.getClientAppEntity().getPath(), e);
                userLogEntity.setTransportStatus(BotMsgTransportStatus.DEFERRED);
                String errStr = "Ошибка при попытке отправить сообщение клиентскому приложению. " +
                        "Попытки отправить сообщение будут продолжены. По факту успешной доставки вам поступит уведомление.";
                responseMsgObject = new MsgObject();
                responseMsgObject.setUserObject(userObject);
                responseMsgObject.setMsgBody(errStr);
            }
        }
        systemService.mergeEntity(userLogEntity);
        botManagerService.sendMessageToBot(responseMsgObject);
    }
}
