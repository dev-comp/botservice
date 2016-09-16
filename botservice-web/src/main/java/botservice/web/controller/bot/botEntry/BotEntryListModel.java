package botservice.web.controller.bot.botEntry;

import botservice.model.bot.BotEntryEntity;
import botservice.properties.BotServicePropertyConst;
import botservice.service.BotService;
import com.bftcom.devcomp.api.IBotManager;
import org.osgi.framework.BundleContext;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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

    @Resource(name = BotServicePropertyConst.OSGI_BUNDLE_CONTEXT_JNDI_NAME)
    private BundleContext bundleContext;


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
        IBotManager botManager = ((IBotManager) bundleContext.getService(bundleContext.getServiceReference(IBotManager.class.getName())));
        botManager.startBotSession(botEntryEntity.getName(), botEntryEntity.getBotAdapterEntity().getProps());
        doSaveBotEntryEntity(botEntryEntity);
    }

    public boolean isStopBotEntryDisabled(BotEntryEntity botEntryEntity){
        return botEntryEntity.getState() == 0;
    }

    public void doStopBotEntry(BotEntryEntity botEntryEntity){
        botEntryEntity.setState(0);
        IBotManager botManager = ((IBotManager) bundleContext.getService(bundleContext.getServiceReference(IBotManager.class.getName())));
        botManager.stopBotSession(botEntryEntity.getName());
        doSaveBotEntryEntity(botEntryEntity);
    }
}
