package botservice.web.controller.bot.botAdapter;

import botservice.model.bot.BotAdapterEntity;
import botservice.service.BotService;

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

    private List<BotAdapterEntity> botAdapterList;

    @PostConstruct
    private void init(){
        botAdapterList = botService.getEntityList(BotAdapterEntity.class);
    }

    public List<BotAdapterEntity> getBotAdapterList(){
        return botAdapterList;
    }

    public void doDeleteBotAdapter(BotAdapterEntity botAdapterEntity){
        botAdapterList.remove(botAdapterEntity);
        botService.removeEntity(botAdapterEntity);
    }

}
