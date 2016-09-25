package botservice.web.controller.bot.botAdapter;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotAdapterEntity_;
import botservice.properties.BotServiceProperty;
import botservice.properties.BotServicePropertyConst;
import botservice.service.BotService;
import botservice.service.common.BaseParam;
import botservice.util.BotAdapterType;
import botservice.web.controller.common.MapItem;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.http.Part;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Компонент-подложка для управления одним адаптером бота
 */

@Named
@ViewScoped
public class BotAdapterEditModel implements Serializable {

    @Inject
    private BotAdapterEntity botAdapterEntity;

    @Inject
    private BotService botService;

    private List<MapItem> propList = new ArrayList<>();

    private List<MapItem> answerList = new ArrayList<>();

    private Part botAdapterPart;

    @Inject
    @BotServiceProperty(name = BotServicePropertyConst.ADAPTER_FILE_PATH)
    private String adapterFilePath;

    @PostConstruct
    public void init(){
        String idParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (idParam != null){
            botAdapterEntity = botService.getEntityByCriteria(BotAdapterEntity.class, new BaseParam(BotAdapterEntity_.id, Long.parseLong(idParam)));
            moveMapItemsFromEntity();
        }
    }

    public void doSaveBotAdapter(){
        if (botAdapterEntity.getProps() != null)
            botAdapterEntity.getProps().clear();
        if (botAdapterEntity.getAnswers() != null)
            botAdapterEntity.getAnswers().clear();
        moveMapItemsToEntity();
        botAdapterEntity = botService.mergeEntity(botAdapterEntity);
    }

    private void moveMapItemsFromEntity(){
        propList.clear();
        answerList.clear();
        for(Map.Entry<String, String> prop: botAdapterEntity.getProps().entrySet())
            propList.add(new MapItem(prop.getKey(), prop.getValue()));
        for(Map.Entry<String, String> answer: botAdapterEntity.getAnswers().entrySet())
            answerList.add(new MapItem(answer.getKey(), answer.getValue()));
    }

    private void moveMapItemsToEntity(){
        if (botAdapterEntity.getProps() == null)
            botAdapterEntity.setProps(new HashMap<String, String>());
        moveMapItemToEntity(propList, botAdapterEntity.getProps());
        if (botAdapterEntity.getAnswers() == null)
            botAdapterEntity.setAnswers(new HashMap<String, String>());
        moveMapItemToEntity(answerList, botAdapterEntity.getAnswers());
    }

    private void moveMapItemToEntity(List<MapItem> mapItems, Map<String, String> itemMap){
        for(MapItem mapItem : mapItems)
            itemMap.put(mapItem.getKey(), mapItem.getValue());
    }

    public void uploadBotAdapter(){
        if (botAdapterPart != null) {
            String header = botAdapterPart.getHeader("content-disposition");
            String botAdapterFileName = null;
            for(String headerPart : header.split(";")) {
                if(headerPart.trim().startsWith("filename")) {
                    botAdapterFileName = headerPart.substring(headerPart.indexOf('=') + 1).trim().replace("\"", "");
                    break;
                }
            }
            Path destination = Paths.get(adapterFilePath + botAdapterFileName);
            try {
                Files.copy(botAdapterPart.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            botAdapterEntity.setFilePath(destination.toString());
        }
    }

    public void doAddAdapterProperty(){
        propList.add(new MapItem("", ""));
    }

    public void doDeleteAdapterProp(MapItem mapItem){
        propList.remove(mapItem);
    }

    public void doAddAdapterAnswer(){
        answerList.add(new MapItem("", ""));
    }

    public void doDeleteAdapterAnswer(MapItem mapItem){
        answerList.remove(mapItem);
    }

    public BotAdapterEntity getBotAdapterEntity() {
        return botAdapterEntity;
    }

    public Part getBotAdapterPart() {
        return botAdapterPart;
    }

    public void setBotAdapterPart(Part botAdapterPart) {
        this.botAdapterPart = botAdapterPart;
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

    public BotAdapterType[] getBotAdapterTypes(){
        return BotAdapterType.values();
    }
}
