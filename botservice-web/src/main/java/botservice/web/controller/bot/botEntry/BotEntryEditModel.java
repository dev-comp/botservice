package botservice.web.controller.bot.botEntry;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotEntryEntity;
import botservice.model.bot.BotEntryEntity_;
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
public class BotEntryEditModel implements Serializable {

    @Inject
    private BotService botService;

    @Inject
    private BotEntryEntity botEntryEntity;

    private List<PropItem> propList = new ArrayList<>();

    private List<BotAdapterEntity> botAdapterEntityList;

    @PostConstruct
    public void init(){
        String idParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (idParam != null){
            botEntryEntity = botService.getEntityByCriteria(BotEntryEntity.class, new BaseParam(BotEntryEntity_.id, Long.parseLong(idParam)));
            movePropsFromEntity();
        }
        botAdapterEntityList = botService.getEntityList(BotAdapterEntity.class);
    }

    public void doSaveBotEntry(){
        if (botEntryEntity.getProps() != null)
            botEntryEntity.getProps().clear();
        movePropsToEntity();
        botEntryEntity = botService.mergeEntity(botEntryEntity);
    }

    private void movePropsFromEntity(){
        propList.clear();
        for(Map.Entry<String, String> entry: botEntryEntity.getProps().entrySet())
            propList.add(new PropItem(entry.getKey(), entry.getValue()));
    }

    private void movePropsToEntity(){
        if (botEntryEntity.getProps() == null)
            botEntryEntity.setProps(new HashMap<String, String>());
        for(PropItem propItem: propList)
            botEntryEntity.getProps().put(propItem.getKey(), propItem.getValue());
    }

    public void doAddEntryProperty(){
        propList.add(new PropItem("", ""));
    }

    public void doDeleteEntryProp(PropItem propItem){
        propList.remove(propItem);
    }

    public BotEntryEntity getBotEntryEntity() {
        return botEntryEntity;
    }

    public void setBotEntryEntity(BotEntryEntity botEntryEntity) {
        this.botEntryEntity = botEntryEntity;
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
