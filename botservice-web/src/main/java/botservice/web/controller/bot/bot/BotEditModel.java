package botservice.web.controller.bot.bot;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotEntity;
import botservice.model.bot.BotEntity_;
import botservice.service.BotService;
import botservice.service.common.BaseParam;
import botservice.web.controller.common.PropItem;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Компонент-подложка для управления экземпляром бота
 */

@Named
@ViewScoped
public class BotEditModel implements Serializable {

    @Inject
    private BotService botService;

    @Inject
    private BotEntity botEntity;

    private List<PropItem> propList = new ArrayList<>();

    private List<BotAdapterEntity> botAdapterEntityList;

    @PostConstruct
    public void init(){
        String idParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (idParam != null){
            botEntity = botService.getEntityByCriteria(BotEntity.class, new BaseParam(BotEntity_.id, Long.parseLong(idParam)));
            movePropsFromEntity();
        }
        botAdapterEntityList = botService.getEntityList(BotAdapterEntity.class);
    }

    public void doSaveBot(){
        if (botEntity.getProps() != null)
            botEntity.getProps().clear();
        movePropsToEntity();
        botEntity = botService.mergeEntity(botEntity);
    }

    private void movePropsFromEntity(){
        propList.clear();
        for(Map.Entry<String, String> prop: botEntity.getProps().entrySet())
            propList.add(new PropItem(prop.getKey(), prop.getValue()));
    }

    private void movePropsToEntity(){
        if (botEntity.getProps() == null)
            botEntity.setProps(new HashMap<String, String>());
        for(PropItem propItem: propList)
            botEntity.getProps().put(propItem.getKey(), propItem.getValue());
    }

    public void doAddBotProperty(){
        propList.add(new PropItem("", ""));
    }

    public void doDeleteBotProp(PropItem propItem){
        propList.remove(propItem);
    }

    public BotEntity getBotEntity() {
        return botEntity;
    }

    public void setBotEntity(BotEntity botEntity) {
        this.botEntity = botEntity;
    }

    public List<BotAdapterEntity> getBotAdapterEntityList() {
        return botAdapterEntityList;
    }

    public void setBotAdapterEntityList(List<BotAdapterEntity> botAdapterEntityList) {
        this.botAdapterEntityList = botAdapterEntityList;
    }

    public List<PropItem> getPropList() {
        return propList;
    }

    public void setPropList(List<PropItem> propList) {
        this.propList = propList;
    }
}
