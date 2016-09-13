package botservice.web.controller.bot.botAdapter;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotAdapterEntity_;
import botservice.properties.BotServiceProperty;
import botservice.properties.BotServicePropertyConst;
import botservice.service.BotService;
import botservice.service.common.BaseParam;

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

    private boolean isNew = true;

    private Part botAdapterPart;

    @Inject
    @BotServiceProperty(name = BotServicePropertyConst.ADAPTER_FILE_PATH)
    private String adapterFilePath;

    @PostConstruct
    public void init(){
        String idParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (idParam != null){
            loadBotAdapterEntity(Long.parseLong(idParam));
            isNew = false;
        }
    }

    private void loadBotAdapterEntity(Long botAdapterEntityId){
        botAdapterEntity = botService.getEntityByCriteria(BotAdapterEntity.class, new BaseParam(BotAdapterEntity_.id, botAdapterEntityId));
    }

    public void doSaveBotAdapter(){
        botAdapterEntity = botService.mergeEntity(botAdapterEntity);
        if (isNew)
            loadBotAdapterEntity(botAdapterEntity.getId());
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
            //todo определиться с местом сохранения адаптера
            Path destination = Paths.get(adapterFilePath + botAdapterFileName);
            try {
                Files.copy(botAdapterPart.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            botAdapterEntity.setFilePath(destination.toString());
        }
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
}
