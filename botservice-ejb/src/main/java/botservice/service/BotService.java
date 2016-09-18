package botservice.service;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotBaseEntity_;
import botservice.model.bot.BotEntryEntity;
import botservice.model.bot.BotEntryEntity_;
import botservice.service.common.BaseParam;
import botservice.service.common.BaseService;

import javax.ejb.Stateless;
import java.util.List;

/**
 * Методы для работы с настройками ботов
 */

@Stateless
public class BotService extends BaseService {

    public List<BotEntryEntity> getActiveAdapterEntriesList(BotAdapterEntity botAdapterEntity){
        return getEntityListByCriteria(BotEntryEntity.class,
                new BaseParam(BotEntryEntity_.botAdapterEntity, botAdapterEntity),
                new BaseParam(BotBaseEntity_.state, 1));
    }

}
