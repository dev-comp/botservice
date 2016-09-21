package botservice.service;

import botservice.model.bot.BotEntryEntity;
import botservice.model.bot.BotEntryEntity_;
import botservice.model.client.ClientAppEntity;
import botservice.model.client.ClientAppEntity_;
import botservice.model.client.ClientEntity;
import botservice.model.client.ClientEntity_;
import botservice.model.system.UserKeyEntity;
import botservice.model.system.UserKeyEntity_;
import botservice.model.system.UserLogEntity;
import botservice.model.system.UserLogEntity_;
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

    private ClientEntity getClientEntityByName(String clientName){
        return getEntityByCriteria(ClientEntity.class,
                new BaseParam(ClientEntity_.name, clientName));
    }

    private List<ClientAppEntity> getClientAppList(ClientEntity clientEntity){
        return getEntityListByCriteria(ClientAppEntity.class,
                new BaseParam(ClientAppEntity_.clientEntity, clientEntity));
    }

    private List<ClientAppEntity> getClientAppList(String clientName){
        return getClientAppList(getClientEntityByName(clientName));
    }

    public List<UserKeyEntity> getUserKeyListByClientName(String clientName){
        List<UserKeyEntity> resultList = new ArrayList<>();
        List<ClientAppEntity> clientAppEntityList = getClientAppList(clientName);
        for (ClientAppEntity clientAppEntity: clientAppEntityList){
            resultList.addAll(getEntityListByCriteria(UserKeyEntity.class,
                    new BaseParam(UserKeyEntity_.botEntryEntity, getEntityByCriteria(BotEntryEntity.class,
                            new BaseParam(BotEntryEntity_.id, clientAppEntity.getBotEntryEntity().getId())))));

        }
        return resultList;
    }

    private List<UserLogEntity> getUserLogEntityListByUserKeyEntity(UserKeyEntity userKeyEntity){
        return getEntityListByCriteria(UserLogEntity.class,
                new BaseParam(UserLogEntity_.userKeyEntity, userKeyEntity));
    }

    public List<UserLogEntity> getUserLogListByClientName(String clientName){
        List<UserLogEntity> resultList = new ArrayList<>();
        for (UserKeyEntity userKeyEntity: getUserKeyListByClientName(clientName))
            resultList.addAll(getUserLogEntityListByUserKeyEntity(userKeyEntity));
        return resultList;
    }

    public List<UserLogEntity> getUserLogListByBotEntryNameAndUserName(String botEntryName, String userName){
        return getUserLogEntityListByUserKeyEntity(getEntityByCriteria(
                UserKeyEntity.class,new BaseParam(UserKeyEntity_.userName, userName),
                new BaseParam(UserKeyEntity_.botEntryEntity, getEntityByCriteria(BotEntryEntity.class,
                        new BaseParam(BotEntryEntity_.name, botEntryName)))));
    }
}
