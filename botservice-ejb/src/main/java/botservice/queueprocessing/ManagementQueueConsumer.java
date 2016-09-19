package botservice.queueprocessing;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotAdapterEntity_;
import botservice.model.bot.BotEntryEntity;
import botservice.service.BotService;
import botservice.service.common.BaseParam;
import com.bftcom.devcomp.bots.BotCommand;
import com.bftcom.devcomp.bots.IBotConst;
import com.bftcom.devcomp.bots.Message;
import com.rabbitmq.client.Channel;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Потребитель управляющих сообщений
 */

public class ManagementQueueConsumer extends CommonQueueConsumer {

    public ManagementQueueConsumer(Channel channel) {
        super(channel);
    }

    @SuppressWarnings("unused")
    IQueueConsumer adapterProcessMessageConsumer = new AbstractQueueConsumer(BotCommand.SERVICE_PROCESS_ENTRY_MESSAGE) {

        @Inject
        BotService botService;

        @Inject
        BotManagerService botManagerService;

        @Override
        public void handleMessage(Message message) throws IOException {
            String adapterName = message.getServiceProperties().get(IBotConst.PROP_ADAPTER_NAME);
            if (adapterName == null)
                throw new RuntimeException("Unknown adapter");
            BotAdapterEntity botAdapterEntity =
                    botService.getEntityByCriteria(BotAdapterEntity.class, new BaseParam(BotAdapterEntity_.name, adapterName));
            if (botAdapterEntity == null)
                throw new RuntimeException("Adapter not found");
            for(BotEntryEntity botEntryEntity: botService.getActiveAdapterEntriesList(botAdapterEntity))
                botManagerService.stopEntrySession(botEntryEntity);
        }
    };


}
