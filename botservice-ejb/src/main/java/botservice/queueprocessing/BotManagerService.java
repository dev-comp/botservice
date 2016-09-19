package botservice.queueprocessing;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotEntryEntity;
import com.bftcom.devcomp.bots.BotCommand;
import com.bftcom.devcomp.bots.IBotConst;
import com.bftcom.devcomp.bots.Message;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */

@Stateless
public class BotManagerService {

    @Inject
    BotManager botManager;

    private boolean sendCommandToAdapter(BotCommand botCommand, BotEntryEntity botEntryEntity){
        Message message = new Message();
        message.setCommand(botCommand);
        Map<String, String> propMap = botEntryEntity.getBotAdapterEntity().getProps();
        propMap.putAll(botEntryEntity.getProps());
        message.setUserProperties(propMap);
        Map<String, String> serviceProperties = new HashMap<>();
        serviceProperties.put(IBotConst.PROP_ENTRY_NAME, botEntryEntity.getName());
        message.setServiceProperties(serviceProperties);
        return sendCommandToBotAdapter(message, botEntryEntity.getBotAdapterEntity());
    }

    private boolean sendCommandToBotAdapter(Message message, BotAdapterEntity botAdapterEntity){
        try {
            String queueName = IBotConst.QUEUE_ADAPTER_PREFIX + botAdapterEntity.getName();
            botManager.getChannel().queueDeclare(queueName, false, false, false, null);
            botManager.getChannel().basicPublish("", queueName, null, IQueueConsumer.mapper.writeValueAsString(message).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public boolean startEntrySession(BotEntryEntity botEntryEntity) {
        return sendCommandToAdapter(BotCommand.ADAPTER_START_ENTRY, botEntryEntity);
    }

    public boolean stopEntrySession(BotEntryEntity botEntryEntity) {
        return sendCommandToAdapter(BotCommand.ADAPTER_STOP_ENTRY, botEntryEntity);
    }

    public boolean stopAllEntries(BotAdapterEntity botAdapterEntity){
        Message message = new Message();
        message.setCommand(BotCommand.ADAPTER_STOP_ALL_ENTRIES);
        return sendCommandToBotAdapter(message, botAdapterEntity);
    }

}
