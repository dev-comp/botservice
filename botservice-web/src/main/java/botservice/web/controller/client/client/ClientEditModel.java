package botservice.web.controller.client.client;

import botservice.model.client.ClientEntity;
import botservice.model.client.ClientEntity_;
import botservice.service.ClientService;
import botservice.service.common.BaseParam;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Компонент-подложка для управления записью о клиенте
 */

@Named
@ViewScoped
public class ClientEditModel implements Serializable {

    @Inject
    private ClientService clientService;

    @Inject
    private ClientEntity clientEntity;

    @PostConstruct
    public void init(){
        String idParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (idParam != null)
            clientEntity = clientService.getEntityByCriteria(ClientEntity.class, new BaseParam(ClientEntity_.id, Long.parseLong(idParam)));
    }

    public void doSaveClient(){
        clientEntity = clientService.mergeEntity(clientEntity);
    }

    public ClientEntity getClientEntity() {
        return clientEntity;
    }

    public void setClientEntity(ClientEntity clientEntity) {
        this.clientEntity = clientEntity;
    }
}
