package botservice.web.controller.bot.bot;

import botservice.model.bot.BotEntity;
import botservice.queueprocessing.BotManagerService;
import botservice.service.BotService;
import botservice.web.controller.common.OSGIService;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
        botEntity.setState(1);
        try {
            botBotList.remove(botEntity);
            botService.removeEntity(botEntity);
            String summary = botEntity.getName() + " bot successfully deleted";
            FacesMessage mess = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, summary);
            FacesContext.getCurrentInstance().addMessage(null, mess);
        } catch (Exception e) {
            FacesMessage mess = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().getMessage(), ExceptionUtils.getStackTrace(e));
            FacesContext.getCurrentInstance().addMessage(null, mess);
        }
    }

    public boolean isStartBotDisabled(BotEntity botEntity) {
        return (botEntity.getState() == 1 || botEntity.getBotAdapterEntity().getState() == 0);
    }

    public void doStartBot(BotEntity botEntity){
        try {
            botEntity.setState(1);
            botManagerService.startBotSession(botEntity);
            doSaveBotEntity(botEntity);
            String summary = botEntity.getName() + " bot successfully started";
            FacesMessage mess = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, summary);
            FacesContext.getCurrentInstance().addMessage(null, mess);
        } catch (Exception e) {
            FacesMessage mess = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().getMessage(), ExceptionUtils.getStackTrace(e));
            FacesContext.getCurrentInstance().addMessage(null, mess);
        }
    }

    public boolean isStopBotDisabled(BotEntity botEntity){
        return (botEntity.getState() == 0 || botEntity.getBotAdapterEntity().getState() == 0);
    }

    public void doStopBot(BotEntity botEntity){
        try {
            botEntity.setState(0);
            botManagerService.stopBotSession(botEntity);
            doSaveBotEntity(botEntity);
            String summary = botEntity.getName() + " bot successfully stopped";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, summary));
        }
        catch (Exception e) {
            FacesMessage mess = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().getMessage(), ExceptionUtils.getStackTrace(e));
            FacesContext.getCurrentInstance().addMessage(null, mess);
        }
    }
}
