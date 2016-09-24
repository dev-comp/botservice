package botservice.queueprocessing;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotAdapterEntity_;
import botservice.model.bot.BotEntity;
import botservice.model.system.UserKeyEntity;
import botservice.model.system.UserKeyEntity_;
import botservice.model.system.UserLogEntity;
import botservice.rest.model.MsgObject;
import botservice.service.BotService;
import botservice.service.SystemService;
import botservice.service.common.BaseParam;
import botservice.util.BotMsgDirectionType;
import botservice.serviceException.ServiceExceptionObject;
import botservice.util.BotMsgTransportStatus;
import com.bftcom.devcomp.api.BotCommand;
import com.bftcom.devcomp.api.IBotConst;
import com.bftcom.devcomp.api.Message;
import botservice.serviceException.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */

@Stateless
public class BotManagerService {
    private static final Logger logger = LoggerFactory.getLogger(BotManagerService.class);


    @Inject
    BotManager botManager;

    @Inject
    BotService botService;

    @Inject
    SystemService systemService;

    @Inject
    @ServiceException
    Event<ServiceExceptionObject> serviceExceptionEvent;

    private boolean sendCommandToAdapter(BotCommand botCommand, BotEntity botEntity){
        Message message = new Message();
        message.setCommand(botCommand);
        Map<String, String> propMap = botEntity.getBotAdapterEntity().getProps();
        propMap.putAll(botEntity.getProps());
        message.setUserProperties(propMap);
        Map<String, String> serviceProperties = new HashMap<>();
        serviceProperties.put(IBotConst.PROP_BOT_NAME, botEntity.getName());
        message.setServiceProperties(serviceProperties);
        return sendCommandToBotAdapter(message, botEntity.getBotAdapterEntity());
    }

    private boolean sendCommandToBotAdapter(Message message, BotAdapterEntity botAdapterEntity){
        try {
            String queueName = IBotConst.QUEUE_TO_ADAPTER_PREFIX + botAdapterEntity.getName();
            botManager.getChannel().queueDeclare(queueName, false, false, false, null);
            botManager.getChannel().basicPublish("", queueName, null, IQueueConsumer.mapper.writeValueAsString(message).getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            serviceExceptionEvent.fire(new ServiceExceptionObject(
                    ("Ошибка при отправке команды  адаптеру"), e));
        }
        return false;
    }

    public boolean startBotSession(BotEntity botEntity) {
        botManager.registerBotQueueConsumer(botEntity.getName());
        return sendCommandToAdapter(BotCommand.ADAPTER_START_BOT, botEntity);
    }

    public boolean stopBotSession(BotEntity botEntity) {
        botManager.unRegisterBotQueueConsumer(botEntity.getName());
        return sendCommandToAdapter(BotCommand.ADAPTER_STOP_BOT, botEntity);
    }

    public boolean stopAllBots(BotAdapterEntity botAdapterEntity){
        Message message = new Message();
        message.setCommand(BotCommand.ADAPTER_STOP_ALL_BOTS);
        return sendCommandToBotAdapter(message, botAdapterEntity);
    }

    public void sendMessageToBot(MsgObject msgObject){
        BotEntity botEntity = botService.getEntityByCriteria(
                BotEntity.class, new BaseParam(BotAdapterEntity_.name, msgObject.getUserObject().getBotName()));
        UserKeyEntity userKeyEntity = botService.getEntityByCriteria(UserKeyEntity.class,
                new BaseParam(UserKeyEntity_.userName, msgObject.getUserObject().getUserName()),
                new BaseParam(UserKeyEntity_.botEntity, botEntity));
        UserLogEntity userLogEntity = new UserLogEntity();
        userLogEntity.setUserKeyEntity(userKeyEntity);
        userLogEntity.setMsgTime(new Date(System.currentTimeMillis()));
        userLogEntity.setMsgBody(msgObject.getMsgBody());
        userLogEntity.setDirectionType(BotMsgDirectionType.TO_USER);
        try {
            systemService.sendMessageToBotQueue(msgObject.getMsgBody(), userKeyEntity);
            userLogEntity.setTransportStatus(BotMsgTransportStatus.DELIVERED);
        } catch (Exception e) {
            userLogEntity.setTransportStatus(BotMsgTransportStatus.DEFERRED);
            logger.error("Ошибка при отправке сообщения боту: " + botEntity.getName(), e);
        }
        systemService.mergeEntity(userLogEntity);
    }
}
