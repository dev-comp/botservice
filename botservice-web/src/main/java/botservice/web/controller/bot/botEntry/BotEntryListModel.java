package botservice.web.controller.bot.botEntry;

import botservice.model.bot.BotEntryEntity;
import botservice.service.BotService;

import javax.annotation.PostConstruct;
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
public class BotEntryListModel implements Serializable {

    @Inject
    private BotService botService;

    private List<BotEntryEntity> botEntryList;

    @PostConstruct
    private void init(){
        botEntryList = botService.getEntityList(BotEntryEntity.class);
    }

    public List<BotEntryEntity> getBotEntryList(){
        return botEntryList;
    }

    public void doDeleteBotEntry(BotEntryEntity botAdapterEntity){
        botEntryList.remove(botAdapterEntity);
        botService.removeEntity(botAdapterEntity);
    }

}
