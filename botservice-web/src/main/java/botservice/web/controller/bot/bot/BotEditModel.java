package botservice.web.controller.bot.bot;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotEntity;
import botservice.model.bot.BotEntity_;
import botservice.service.BotService;
import botservice.service.common.BaseParam;
import botservice.web.controller.common.MapItem;

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

    private List<MapItem> propList = new ArrayList<>();

    private List<MapItem> answerList = new ArrayList<>();

    private List<BotAdapterEntity> botAdapterEntityList;

    @PostConstruct
    public void init(){
        String idParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (idParam != null){
            botEntity = botService.getEntityByCriteria(BotEntity.class, new BaseParam(BotEntity_.id, Long.parseLong(idParam)));
            moveMapItemsFromEntity();
        }
        botAdapterEntityList = botService.getEntityList(BotAdapterEntity.class);
    }

    public void doSaveBot(){
        if (botEntity.getProps() != null)
            botEntity.getProps().clear();
        if (botEntity.getAnswers() != null)
            botEntity.getAnswers().clear();
        moveMapItemsToEntity();
        botEntity = botService.mergeEntity(botEntity);
    }

    private void moveMapItemsFromEntity(){
        propList.clear();
        answerList.clear();
        for(Map.Entry<String, String> prop: botEntity.getProps().entrySet())
            propList.add(new MapItem(prop.getKey(), prop.getValue()));
        for(Map.Entry<String, String> answer: botEntity.getAnswers().entrySet())
            answerList.add(new MapItem(answer.getKey(), answer.getValue()));
    }

    private void moveMapItemsToEntity(){
        if (botEntity.getProps() == null)
            botEntity.setProps(new HashMap<String, String>());
        moveMapItemToEntity(propList, botEntity.getProps());
        if (botEntity.getAnswers() == null)
            botEntity.setAnswers(new HashMap<String, String>());
        moveMapItemToEntity(answerList, botEntity.getAnswers());
    }

    private void moveMapItemToEntity(List<MapItem> mapItems, Map<String, String> itemMap){
        for(MapItem mapItem : mapItems)
            itemMap.put(mapItem.getKey(), mapItem.getValue());
    }

    public void doAddBotProperty(){
        propList.add(new MapItem("", ""));
    }

    public void doDeleteBotProp(MapItem mapItem){
        propList.remove(mapItem);
    }

    public void doAddBotAnswer(){
        answerList.add(new MapItem("", ""));
    }

    public void doDeleteBotAnswer(MapItem mapItem){
        answerList.remove(mapItem);
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

    public List<MapItem> getPropList() {
        return propList;
    }

    public void setPropList(List<MapItem> propList) {
        this.propList = propList;
    }

    public List<MapItem> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<MapItem> answerList) {
        this.answerList = answerList;
    }
}
