package botservice.web.controller.client.client;

import botservice.model.client.ClientEntity;
import botservice.service.ClientService;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Компонент-подложка для управления списком клиентов
 */

@Named
@ViewScoped
public class ClientListModel implements Serializable {

    @Inject
    private ClientService clientService;

    private List<ClientEntity> clientList;

    @PostConstruct
    private void init(){
        clientList = clientService.getEntityList(ClientEntity.class);
    }

    public List<ClientEntity> getClientList() {
        return clientList;
    }

    public void setClientList(List<ClientEntity> clientList) {
        this.clientList = clientList;
    }

    public void doDeleteClient(ClientEntity clientEntity){
        clientList.remove(clientEntity);
        clientService.removeEntity(clientEntity);
    }
}
