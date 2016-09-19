package botservice.web.controller.bot.botAdapter;

import botservice.model.bot.*;
import botservice.service.BotService;
import botservice.service.common.BaseParam;
import botservice.web.controller.queueprocessing.BotManager;
import botservice.web.controller.common.OSGIService;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Компонент-подложка для управления списком адаптеров ботов
 */

@Named
@ViewScoped
public class BotAdapterListModel implements Serializable{

    @Inject
    private BotService botService;

    @Inject
    private OSGIService osgiService;

    @Inject
    BotManager botManager;

    private List<BotAdapterEntity> botAdapterList;

    @PostConstruct
    private void init(){
        botAdapterList = botService.getEntityList(BotAdapterEntity.class);
    }

    public List<BotAdapterEntity> getBotAdapterList(){
        return botAdapterList;
    }

    private void doSaveBotAdapterEntity (BotAdapterEntity botAdapterEntity){
        BotAdapterEntity updatedBotAdapterEntity = botService.mergeEntity(botAdapterEntity);
        botAdapterEntity.setVersion(updatedBotAdapterEntity.getVersion());
    }

    public boolean isDeleteBotAdapterDisabled(BotAdapterEntity botAdapterEntity){
        return !osgiService.isBundleInstalled(botAdapterEntity.getName());
    }

    public void doDeleteBotAdapter(BotAdapterEntity botAdapterEntity){
        botAdapterList.remove(botAdapterEntity);
        botService.removeEntity(botAdapterEntity);
    }

    public boolean isInstallBotAdapterDisabled(BotAdapterEntity botAdapterEntity){
        return osgiService.isBundleInstalled(botAdapterEntity.getName());
    }

    public void doInstallBotAdapter(BotAdapterEntity botAdapterEntity){
        osgiService.installBotAdapter(botAdapterEntity.getFilePath(), botAdapterEntity.getName());
    }

    public boolean isStartBotAdapterDisabled(BotAdapterEntity botAdapterEntity){
        return (!osgiService.isBundleInstalled(botAdapterEntity.getName())) || (botAdapterEntity.getState() == 1);
    }

    public void doStartBotAdapter(BotAdapterEntity botAdapterEntity){
        if (osgiService.startBotAdapter(botAdapterEntity.getName())){
            botAdapterEntity.setState(1);
            doSaveBotAdapterEntity(botAdapterEntity);
        }
    }

    public boolean isStopBotAdapterDisabled(BotAdapterEntity botAdapterEntity){
        return (!osgiService.isBundleInstalled(botAdapterEntity.getName())) || (botAdapterEntity.getState() == 0);
    }

    public void doStopBotAdapter(BotAdapterEntity botAdapterEntity){
        if (osgiService.stopBotAdapter(botAdapterEntity.getName())){
            List<BotEntryEntity> botEntryList = botService.getActiveAdapterEntriesList(botAdapterEntity);
            for (BotEntryEntity botEntryEntity: botEntryList){
                botEntryEntity.setState(0);
                botService.mergeEntity(botEntryEntity);
            }
            botAdapterEntity.setState(0);
            doSaveBotAdapterEntity(botAdapterEntity);
            botManager.stopAllEntries(botAdapterEntity);
        }
    }

    public boolean isUninstallBotAdapterDisabled(BotAdapterEntity botAdapterEntity){
        return (   (osgiService.isBundleInstalled(botAdapterEntity.getName()) && (botAdapterEntity.getState() == 1))
                || !osgiService.isBundleInstalled(botAdapterEntity.getName()));
    }

    public void doUninstallBotAdapter(BotAdapterEntity botAdapterEntity){
        osgiService.uninstallBotAdapter(botAdapterEntity.getName());
    }
}
