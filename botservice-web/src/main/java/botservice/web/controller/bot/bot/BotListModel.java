package botservice.web.controller.bot.bot;

import botservice.model.bot.BotEntity;
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
public class BotListModel implements Serializable {

    @Inject
    private BotService botService;

    @Inject
    OSGIService osgiService;

    @Inject
    private BotManagerService botManagerService;

    private List<BotEntity> botBotList;

    @PostConstruct
    private void init(){
        botBotList = botService.getEntityList(BotEntity.class);
    }

    private void doSaveBotEntity(BotEntity botEntity){
        BotEntity updatedBotEntity = botService.mergeEntity(botEntity);
        botEntity.setVersion(updatedBotEntity.getVersion());
    }

    public boolean isDeleteBotDisabled(BotEntity botEntity){
        return (botEntity.getState() == 1);
    }

    public List<BotEntity> getBotList(){
        return botBotList;
    }

    public void doDeleteBot(BotEntity botEntity){
        botBotList.remove(botEntity);
        botService.removeEntity(botEntity);
    }

    public boolean isStartBotDisabled(BotEntity botEntity) {
        return (botEntity.getState() == 1 || botEntity.getBotAdapterEntity().getState() == 0);
    }

    public void doStartBot(BotEntity botEntity){
        botEntity.setState(1);
        botManagerService.startBotSession(botEntity);
        doSaveBotEntity(botEntity);
    }

    public boolean isStopBotDisabled(BotEntity botEntity){
        return (botEntity.getState() == 0 || botEntity.getBotAdapterEntity().getState() == 0);
    }

    public void doStopBot(BotEntity botEntity){
        botEntity.setState(0);
        botManagerService.stopBotSession(botEntity);
        doSaveBotEntity(botEntity);
    }
}
