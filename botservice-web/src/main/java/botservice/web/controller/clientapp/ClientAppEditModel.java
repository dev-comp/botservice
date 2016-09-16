package botservice.web.controller.clientapp;

import botservice.model.bot.BotEntryEntity;
import botservice.model.clientapp.ClientAppEntity;
import botservice.model.clientapp.ClientAppEntity_;
import botservice.service.ClientAppService;
import botservice.service.common.BaseParam;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Компонент-подложка для управления записью о клиентском приложении
 */

@Named
@ViewScoped
public class ClientAppEditModel implements Serializable {

    @Inject
    private ClientAppService clientAppService;

    @Inject
    private ClientAppEntity clientAppEntity;

    private List<BotEntryEntity> botEntryEntityList;

    @PostConstruct
    public void init(){
        String idParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (idParam != null)
            clientAppEntity = clientAppService.getEntityByCriteria(ClientAppEntity.class, new BaseParam(ClientAppEntity_.id, Long.parseLong(idParam)));
        botEntryEntityList = clientAppService.getEntityList(BotEntryEntity.class);
    }

    public void doSaveClientApp(){
        clientAppEntity = clientAppService.mergeEntity(clientAppEntity);
    }

    public ClientAppEntity getClientAppEntity() {
        return clientAppEntity;
    }

    public void setClientAppEntity(ClientAppEntity clientAppEntity) {
        this.clientAppEntity = clientAppEntity;
    }

    public List<BotEntryEntity> getBotEntryEntityList() {
        return botEntryEntityList;
    }

    public void setBotEntryEntityList(List<BotEntryEntity> botEntryEntityList) {
        this.botEntryEntityList = botEntryEntityList;
    }
}
