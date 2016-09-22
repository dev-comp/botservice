package botservice.web.controller.system;

import botservice.model.system.UserLogEntity;
import botservice.service.SystemService;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Компонент-подложка для отображения лога сообщений
 */

@Named
@ViewScoped
public class UserLogListModel implements Serializable {

    @Inject
    SystemService systemService;

    Set<UserLogEntity> userLogSet = new TreeSet<>(new Comparator<UserLogEntity>() {
        @Override
        public int compare(UserLogEntity o1, UserLogEntity o2) {
            return ((-1) * o1.getMsgTime().compareTo(o2.getMsgTime()));
        }
    });

    private int maxResult = 100;

    @PostConstruct
    private void init(){
        refreshList();
    }

    public void refreshList(){
        userLogSet.clear();
        userLogSet.addAll(systemService.getEntityList(UserLogEntity.class, maxResult));
    }

    public Set<UserLogEntity> getUserLogSet() {
        return userLogSet;
    }

    public void setUserLogSet(Set<UserLogEntity> userLogSet) {
        this.userLogSet = userLogSet;
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
