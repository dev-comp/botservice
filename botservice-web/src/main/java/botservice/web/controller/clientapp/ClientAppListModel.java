package botservice.web.controller.clientapp;

import botservice.model.clientapp.ClientAppEntity;
import botservice.service.ClientAppService;

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
    private ClientAppService clientAppService;

    private List<ClientAppEntity> clientAppList;

    @PostConstruct
    private void init(){
        clientAppList = clientAppService.getEntityList(ClientAppEntity.class);
    }

    public List<ClientAppEntity> getClientAppList() {
        return clientAppList;
    }

    public void setClientAppList(List<ClientAppEntity> clientAppList) {
        this.clientAppList = clientAppList;
    }

    public void doDeleteClientApp(ClientAppEntity clientAppEntity){
        clientAppList.remove(clientAppEntity);
        clientAppService.removeEntity(clientAppEntity);
    }
}
