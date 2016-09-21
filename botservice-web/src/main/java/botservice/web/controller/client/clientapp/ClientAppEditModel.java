package botservice.web.controller.client.clientapp;

import botservice.model.bot.BotEntity;
import botservice.model.client.ClientAppEntity;
import botservice.model.client.ClientAppEntity_;
import botservice.model.client.ClientEntity;
import botservice.service.ClientService;
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
    private ClientService clientService;

    @Inject
    private ClientAppEntity clientAppEntity;

    private List<BotEntity> botEntityList;

    private List<ClientEntity> clientEntityList;

    @PostConstruct
    public void init(){
        String idParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (idParam != null)
            clientAppEntity = clientService.getEntityByCriteria(ClientAppEntity.class, new BaseParam(ClientAppEntity_.id, Long.parseLong(idParam)));
        botEntityList = clientService.getEntityList(BotEntity.class);
        clientEntityList = clientService.getEntityList(ClientEntity.class);
    }

    public void doSaveClientApp(){
        clientAppEntity = clientService.mergeEntity(clientAppEntity);
    }

    public ClientAppEntity getClientAppEntity() {
        return clientAppEntity;
    }

    public void setClientAppEntity(ClientAppEntity clientAppEntity) {
        this.clientAppEntity = clientAppEntity;
    }

    public List<BotEntity> getBotEntityList() {
        return botEntityList;
    }

    public void setBotEntityList(List<BotEntity> botEntityList) {
        this.botEntityList = botEntityList;
    }

    public List<ClientEntity> getClientEntityList() {
        return clientEntityList;
    }

    public void setClientEntityList(List<ClientEntity> clientEntityList) {
        this.clientEntityList = clientEntityList;
    }
}
