package botservice.web.controller.system;

import botservice.model.system.UserLogEntity;
import botservice.model.system.UserLogEntity_;
import botservice.service.SystemService;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.metamodel.SingularAttribute;
import java.io.Serializable;
import java.util.*;

/**
 * Компонент-подложка для отображения лога сообщений
 */

@Named
@ViewScoped
public class UserLogListModel implements Serializable {

    @Inject
    SystemService systemService;

    List<UserLogEntity> userLogList = new ArrayList<>();

    private int maxResult = 100;

    @PostConstruct
    private void init(){
        refreshList();
    }

    public void refreshList(){
        userLogList.clear();
        userLogList.addAll(systemService.getEntityList(UserLogEntity.class, maxResult,
                null, new SingularAttribute[]{UserLogEntity_.msgTime}));
    }

    public int getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }

    public void clearAll(){
        systemService.removeAllEntities(UserLogEntity.class);
        refreshList();
    }

    public List<UserLogEntity> getUserLogList() {
        return userLogList;
    }

    public void setUserLogList(List<UserLogEntity> userLogList) {
        this.userLogList = userLogList;
    }
}
