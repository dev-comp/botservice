package botservice.queueprocessing;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotAdapterEntity_;
import botservice.model.bot.BotEntity;
import botservice.service.BotService;
import botservice.service.common.BaseParam;
import botservice.serviceException.ServiceExceptionObject;
import com.bftcom.devcomp.bots.IBotConst;
import com.bftcom.devcomp.bots.Message;
import botservice.serviceException.ServiceException;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Обработчик служебных запросов активных экземпляров ботов от адаптеров
 */
@Stateless
public class ActiveBotGetterHandler {

    @Inject
    BotService botService;

    @Inject
    BotManagerService botManagerService;

    @Inject
    @ServiceException
    Event<ServiceExceptionObject> serviceExceptionEvent;

    public void handleMessage(@Observes @ActiveBotGetter Message message){
        String adapterName = message.getServiceProperties().get(IBotConst.PROP_ADAPTER_NAME);
        try {
            BotAdapterEntity botAdapterEntity =
                    botService.getEntityByCriteria(BotAdapterEntity.class, new BaseParam(BotAdapterEntity_.name, adapterName));
            for(BotEntity botEntity : botService.getActiveBotList(botAdapterEntity))
                botManagerService.stopBotSession(botEntity);
        } catch (Exception e){
            serviceExceptionEvent.fire(new ServiceExceptionObject(
                    "Ошибка при обработке запроса активных ботов: " + adapterName, e));
        }
    }

}
