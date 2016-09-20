package botservice.service;

import botservice.model.bot.BotEntryEntity;
import botservice.model.bot.BotEntryEntity_;
import botservice.model.client.ClientAppEntity;
import botservice.model.client.ClientAppEntity_;
import botservice.model.client.ClientEntity;
import botservice.model.client.ClientEntity_;
import botservice.model.system.UserKeyEntity;
import botservice.model.system.UserKeyEntity_;
import botservice.service.common.BaseParam;
import botservice.service.common.BaseService;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.beans.Expression;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Методы для работы со сведениями о клиентских приложениях
 */

@Stateless
public class ClientService extends BaseService {



    public List<UserKeyEntity> getUserKeyListByClientName(String clientName){
        List<UserKeyEntity> resultList = new ArrayList<>();
        ClientEntity clientEntity = getEntityByCriteria(ClientEntity.class,
                new BaseParam(ClientEntity_.name, clientName));
        List<ClientAppEntity> clientAppEntityList = getEntityListByCriteria(ClientAppEntity.class,
                new BaseParam(ClientAppEntity_.clientEntity, clientEntity));
        for (ClientAppEntity clientAppEntity: clientAppEntityList){
            resultList.addAll(getEntityListByCriteria(UserKeyEntity.class,
                    new BaseParam(UserKeyEntity_.botEntryEntity, getEntityByCriteria(BotEntryEntity.class,
                            new BaseParam(BotEntryEntity_.id, clientAppEntity.getBotEntryEntity().getId())))));

        }
        return resultList;
    }
}
