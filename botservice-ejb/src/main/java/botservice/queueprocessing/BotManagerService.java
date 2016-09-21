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
import com.bftcom.devcomp.bots.BotCommand;
import com.bftcom.devcomp.bots.IBotConst;
import com.bftcom.devcomp.bots.Message;
import botservice.serviceException.ServiceException;

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
        try {
            Message message = new Message();
            message.setCommand(botCommand);
            Map<String, String> propMap = botEntity.getBotAdapterEntity().getProps();
            propMap.putAll(botEntity.getProps());
            message.setUserProperties(propMap);
            Map<String, String> serviceProperties = new HashMap<>();
            serviceProperties.put(IBotConst.PROP_BOT_NAME, botEntity.getName());
            message.setServiceProperties(serviceProperties);
            return sendCommandToBotAdapter(message, botEntity.getBotAdapterEntity());
        } catch (Exception e){
            serviceExceptionEvent.fire(new ServiceExceptionObject(
                    ("Ошибка при отправке команды " + botCommand.name() + " адаптеру"), e));
        }
        return false;
    }

    private boolean sendCommandToBotAdapter(Message message, BotAdapterEntity botAdapterEntity){
        try {
            String queueName = IBotConst.QUEUE_ADAPTER_PREFIX + botAdapterEntity.getName();
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
        return sendCommandToAdapter(BotCommand.ADAPTER_START_BOT, botEntity);
    }

    public boolean stopBotSession(BotEntity botEntity) {
        return sendCommandToAdapter(BotCommand.ADAPTER_STOP_BOT, botEntity);
    }

    public boolean stopAllEntries(BotAdapterEntity botAdapterEntity){
        Message message = new Message();
        message.setCommand(BotCommand.ADAPTER_STOP_ALL_ENTRIES);
        return sendCommandToBotAdapter(message, botAdapterEntity);
    }

    public boolean sendMessageToBot(MsgObject msgObject){
        try {
            BotEntity botEntity = botService.getEntityByCriteria(
                    BotEntity.class, new BaseParam(BotAdapterEntity_.name, msgObject.getUserObject().getBotName()));
            UserKeyEntity userKeyEntity = botService.getEntityByCriteria(UserKeyEntity.class,
                    new BaseParam(UserKeyEntity_.userName, msgObject.getUserObject().getUserName()),
                    new BaseParam(UserKeyEntity_.botEntity, botEntity));
            // отправляем сообщение
            Message message = new Message();
            message.setCommand(BotCommand.SERVICE_PROCESS_BOT_MESSAGE);
            message.setServiceProperties(userKeyEntity.getProps());
            message.getUserProperties().put(IBotConst.PROP_BODY_TEXT, msgObject.getMsgBody());
            try {
                String queueName = IBotConst.QUEUE_BOT_PREFIX + botEntity.getName();
                botManager.getChannel().queueDeclare(queueName, false, false, false, null);
                botManager.getChannel().basicPublish("", queueName, null, IQueueConsumer.mapper.writeValueAsString(message).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // логируем
            UserLogEntity userLogEntity = new UserLogEntity();
            userLogEntity.setUserKeyEntity(userKeyEntity);
            userLogEntity.setMsgTime(new Date(System.currentTimeMillis()));
            userLogEntity.setMsgBody(msgObject.getMsgBody());
            userLogEntity.setDirectionType(BotMsgDirectionType.OUT);
            systemService.mergeEntity(userLogEntity);
            return true;
        } catch (Exception e){
            serviceExceptionEvent.fire(new ServiceExceptionObject(
                    ("Ошибка при отправке сообщения боту"), e));
        }
        return false;
    }

}
