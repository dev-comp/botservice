package botservice.web.controller.system;

import botservice.model.system.UserLogEntity;
import botservice.service.SystemService;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Компонент-подложка для отображения лога сообщений
 */

@Named
@ViewScoped
public class UserLogListModel implements Serializable {

    @Inject
    SystemService systemService;

    List<UserLogEntity> userLogList;

    private int maxResult = 100;

    @PostConstruct
    private void init(){
        refreshList();
    }

    public void refreshList(){
        userLogList = systemService.getEntityList(UserLogEntity.class, maxResult);
    }

    public List<UserLogEntity> getUserLogList() {
        return userLogList;
    }

    public void setUserLogList(List<UserLogEntity> userLogList) {
        this.userLogList = userLogList;
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

}
