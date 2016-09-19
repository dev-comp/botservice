package botservice.web.controller.client.clientapp;

import botservice.model.client.ClientAppEntity;
import botservice.service.ClientService;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Компонент-подложка для управления списком клиентских приложений
 */

@Named
@ViewScoped
public class ClientAppListModel implements Serializable {

    @Inject
    private ClientService clientService;

    private List<ClientAppEntity> clientAppList;

    @PostConstruct
    private void init(){
        clientAppList = clientService.getEntityList(ClientAppEntity.class);
    }

    public List<ClientAppEntity> getClientAppList() {
        return clientAppList;
    }

    public void setClientAppList(List<ClientAppEntity> clientAppList) {
        this.clientAppList = clientAppList;
    }

    public void doDeleteClientApp(ClientAppEntity clientAppEntity){
        clientAppList.remove(clientAppEntity);
        clientService.removeEntity(clientAppEntity);
    }
}
