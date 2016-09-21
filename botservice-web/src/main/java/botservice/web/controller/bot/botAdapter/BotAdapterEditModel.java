package botservice.web.controller.bot.botAdapter;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotAdapterEntity_;
import botservice.properties.BotServiceProperty;
import botservice.properties.BotServicePropertyConst;
import botservice.service.BotService;
import botservice.service.common.BaseParam;
import botservice.web.controller.common.PropItem;

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

    private List<PropItem> propList = new ArrayList<>();

    private Part botAdapterPart;

    @Inject
    @BotServiceProperty(name = BotServicePropertyConst.ADAPTER_FILE_PATH)
    private String adapterFilePath;

    @PostConstruct
    public void init(){
        String idParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (idParam != null){
            botAdapterEntity = botService.getEntityByCriteria(BotAdapterEntity.class, new BaseParam(BotAdapterEntity_.id, Long.parseLong(idParam)));
            movePropsFromEntity();
        }
    }

    public void doSaveBotAdapter(){
        if (botAdapterEntity.getProps() != null)
            botAdapterEntity.getProps().clear();
        movePropsToEntity();
        botAdapterEntity = botService.mergeEntity(botAdapterEntity);
    }

    private void movePropsFromEntity(){
        propList.clear();
        for(Map.Entry<String, String> prop: botAdapterEntity.getProps().entrySet())
            propList.add(new PropItem(prop.getKey(), prop.getValue()));
    }

    private void movePropsToEntity(){
        if (botAdapterEntity.getProps() == null)
            botAdapterEntity.setProps(new HashMap<String, String>());
        for(PropItem propItem: propList)
            botAdapterEntity.getProps().put(propItem.getKey(), propItem.getValue());
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
        propList.add(new PropItem("", ""));
    }

    public void doDeleteAdapterProp(PropItem propItem){
        propList.remove(propItem);
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

    public List<PropItem> getPropList() {
        return propList;
    }

    public void setPropList(List<PropItem> propList) {
        this.propList = propList;
    }
}
