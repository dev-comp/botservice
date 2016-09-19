package botservice.web.controller.bot.botEntry;

import botservice.model.bot.BotEntryEntity;
import botservice.queueprocessing.BotManagerService;
import botservice.service.BotService;
import botservice.web.controller.common.OSGIService;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Компонент-подложка для управления списком экземпляров ботов
 */

@Named
@ViewScoped
public class BotEntryListModel implements Serializable {

    @Inject
    private BotService botService;

    @Inject
    OSGIService osgiService;

    @Inject
    private BotManagerService botManagerService;

    private List<BotEntryEntity> botEntryList;

    @PostConstruct
    private void init(){
        botEntryList = botService.getEntityList(BotEntryEntity.class);
    }

    private void doSaveBotEntryEntity (BotEntryEntity botEntryEntity){
        BotEntryEntity updatedBotEntryEntity = botService.mergeEntity(botEntryEntity);
        botEntryEntity.setVersion(updatedBotEntryEntity.getVersion());
    }

    public boolean isDeleteBotEntryDisabled(BotEntryEntity botEntryEntity){
        return botEntryEntity.getState() == 1;
    }

    public List<BotEntryEntity> getBotEntryList(){
        return botEntryList;
    }

    public void doDeleteBotEntry(BotEntryEntity botEntryEntity){
        botEntryList.remove(botEntryEntity);
        botService.removeEntity(botEntryEntity);
    }

    public boolean isStartBotEntryDisabled(BotEntryEntity botEntryEntity) {
        return botEntryEntity.getState() == 1;
    }

    public void doStartBotEntry(BotEntryEntity botEntryEntity){
        botEntryEntity.setState(1);
        botManagerService.startEntrySession(botEntryEntity);
        doSaveBotEntryEntity(botEntryEntity);
    }

    public boolean isStopBotEntryDisabled(BotEntryEntity botEntryEntity){
        return botEntryEntity.getState() == 0;
    }

    public void doStopBotEntry(BotEntryEntity botEntryEntity){
        botEntryEntity.setState(0);
        botManagerService.stopEntrySession(botEntryEntity);
        doSaveBotEntryEntity(botEntryEntity);
    }
}
