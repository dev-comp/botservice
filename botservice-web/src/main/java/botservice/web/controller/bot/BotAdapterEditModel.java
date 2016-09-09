package botservice.web.controller.bot;

import botservice.model.bot.BotAdapterEntity;
import botservice.model.bot.BotAdapterEntity_;
import botservice.service.BotService;
import botservice.service.common.BaseParam;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

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

    @PostConstruct
    public void init(){
        String idParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (idParam != null){
            loadBotAdapterEntity(Long.parseLong(idParam));
            isNew = false;
        }
    }

    public BotAdapterEntity getBotAdapterEntity() {
        return botAdapterEntity;
    }

    private void loadBotAdapterEntity(Long botAdapterEntityId){
        botAdapterEntity = botService.getEntityByCriteria(BotAdapterEntity.class, new BaseParam(BotAdapterEntity_.id, botAdapterEntityId));
    }

    public void doSaveBotAdapter(){
        botAdapterEntity = botService.mergeEntity(botAdapterEntity);
        if (isNew)
            loadBotAdapterEntity(botAdapterEntity.getId());
    }
}
