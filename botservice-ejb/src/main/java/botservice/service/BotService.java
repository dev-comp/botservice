package botservice.service;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotBaseEntity_;
import botservice.model.bot.BotEntity;
import botservice.model.bot.BotEntity_;
import botservice.service.common.BaseParam;
import botservice.service.common.BaseService;

import javax.ejb.Stateless;
import java.util.List;

/**
 * Методы для работы с настройками ботов
 */

@Stateless
public class BotService extends BaseService {

    public List<BotEntity> getActiveAdapterEntriesList(BotAdapterEntity botAdapterEntity){
        return getEntityListByCriteria(BotEntity.class,
                new BaseParam(BotEntity_.botAdapterEntity, botAdapterEntity),
                new BaseParam(BotBaseEntity_.state, 1));
    }

}
