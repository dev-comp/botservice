package botservice.web.controller.bot.botEntry;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotEntryEntity;
import botservice.model.bot.BotEntryEntity_;
import botservice.service.BotService;
import botservice.service.common.BaseParam;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

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

    private List<BotAdapterEntity> botAdapterEntityList;

    @PostConstruct
    public void init(){
        String idParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (idParam != null)
            botEntryEntity = botService.getEntityByCriteria(BotEntryEntity.class, new BaseParam(BotEntryEntity_.id, Long.parseLong(idParam)));
        botAdapterEntityList = botService.getEntityList(BotAdapterEntity.class);
    }

    public void doSaveBotEntry(){
        botEntryEntity = botService.mergeEntity(botEntryEntity);
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
}
