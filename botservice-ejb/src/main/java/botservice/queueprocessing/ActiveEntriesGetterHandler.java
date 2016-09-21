package botservice.queueprocessing;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotAdapterEntity_;
import botservice.model.bot.BotEntryEntity;
import botservice.service.BotService;
import botservice.service.common.BaseParam;
import com.bftcom.devcomp.bots.IBotConst;
import com.bftcom.devcomp.bots.Message;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Обработчик служебных запросов активных экземпляров ботов от адаптеров
 */
public class ActiveEntriesGetterHandler {

    @Inject
    BotService botService;

    @Inject
    BotManagerService botManagerService;

    public void handleMessage(@Observes @ActiveEntriesGetter Message message){
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

}
