package botservice.web.controller.system;

import botservice.model.system.UserKeyEntity;
import botservice.model.system.UserKeyEntity_;
import botservice.service.SystemService;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.metamodel.SingularAttribute;
import java.io.Serializable;
import java.util.List;

/**
 * Компонент-подложка для отображения списка пользователей
 */

@Named
@ViewScoped
public class UserKeyListModel implements Serializable{

    @Inject
    SystemService systemService;

    List<UserKeyEntity> userKeyList;

    private int maxResult = 100;

    @PostConstruct
    private void init(){
        refreshList();
    }

    public void refreshList(){
        userKeyList = systemService.getEntityList(UserKeyEntity.class, maxResult,
                new SingularAttribute[]{UserKeyEntity_.userName}, null);
    }

    public List<UserKeyEntity> getUserKeyList() {
        return userKeyList;
    }

    public void setUserKeyList(List<UserKeyEntity> userKeyList) {
        this.userKeyList = userKeyList;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }

    public void clearAll(){
        systemService.removeAllEntities(UserKeyEntity.class);
        refreshList();
    }

}
