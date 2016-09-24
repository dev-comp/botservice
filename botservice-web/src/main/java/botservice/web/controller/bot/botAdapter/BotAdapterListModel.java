package botservice.web.controller.bot.botAdapter;

import botservice.model.bot.*;
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
    BotManagerService botManagerService;

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
        return osgiService.isBundleInstalled(botAdapterEntity.getName());
    }

    public void doDeleteBotAdapter(BotAdapterEntity botAdapterEntity){
        try {
            botAdapterList.remove(botAdapterEntity);
            botService.removeEntity(botAdapterEntity);
            String summary = botAdapterEntity.getName() + " bot adapter successfully deleted";
            FacesMessage mess = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, summary);
            FacesContext.getCurrentInstance().addMessage(null, mess);
        } catch (Exception e) {
            FacesMessage mess = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().getMessage(), ExceptionUtils.getStackTrace(e));
            FacesContext.getCurrentInstance().addMessage(null, mess);
        }
    }

    public boolean isInstallBotAdapterDisabled(BotAdapterEntity botAdapterEntity){
        return osgiService.isBundleInstalled(botAdapterEntity.getName());
    }

    public void doInstallBotAdapter(BotAdapterEntity botAdapterEntity){
        try {
            osgiService.installBotAdapter(botAdapterEntity.getFilePath(), botAdapterEntity.getName());
            String summary = botAdapterEntity.getName() + " bot adapter successfully installed";
            FacesMessage mess = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, summary);
            FacesContext.getCurrentInstance().addMessage(null, mess);
        } catch (Exception e) {
            FacesMessage mess = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().getMessage(), ExceptionUtils.getStackTrace(e));
            FacesContext.getCurrentInstance().addMessage(null, mess);
        }
    }

    public boolean isStartBotAdapterDisabled(BotAdapterEntity botAdapterEntity){
        return (!osgiService.isBundleInstalled(botAdapterEntity.getName())) || (botAdapterEntity.getState() == 1);
    }

    public void doStartBotAdapter(BotAdapterEntity botAdapterEntity){
        try {
            if (osgiService.startBotAdapter(botAdapterEntity.getName())){
                botAdapterEntity.setState(1);
                doSaveBotAdapterEntity(botAdapterEntity);
            }
            String summary = botAdapterEntity.getName() + " bot adapter successfully started";
            FacesMessage mess = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, summary);
            FacesContext.getCurrentInstance().addMessage(null, mess);
        } catch (Exception e) {
            FacesMessage mess = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().getMessage(), ExceptionUtils.getStackTrace(e));
            FacesContext.getCurrentInstance().addMessage(null, mess);
        }
    }

    public boolean isStopBotAdapterDisabled(BotAdapterEntity botAdapterEntity){
        return (!osgiService.isBundleInstalled(botAdapterEntity.getName())) || (botAdapterEntity.getState() == 0);
    }

    public void doStopBotAdapter(BotAdapterEntity botAdapterEntity){
        try {
            if (osgiService.stopBotAdapter(botAdapterEntity.getName())){
                List<BotEntity> botList = botService.getActiveBotList(botAdapterEntity);
                for (BotEntity botEntity : botList){
                    botEntity.setState(0);
                    botService.mergeEntity(botEntity);
                }
                botAdapterEntity.setState(0);
                doSaveBotAdapterEntity(botAdapterEntity);
                botManagerService.stopAllBots(botAdapterEntity);
            }
            String summary = botAdapterEntity.getName() + " bot adapter successfully stoped";
            FacesMessage mess = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, summary);
            FacesContext.getCurrentInstance().addMessage(null, mess);
        } catch (Exception e) {
            FacesMessage mess = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().getMessage(), ExceptionUtils.getStackTrace(e));
            FacesContext.getCurrentInstance().addMessage(null, mess);
        }
    }

    public boolean isUninstallBotAdapterDisabled(BotAdapterEntity botAdapterEntity){
        return (   (osgiService.isBundleInstalled(botAdapterEntity.getName()) && (botAdapterEntity.getState() == 1))
                || !osgiService.isBundleInstalled(botAdapterEntity.getName()));
    }

    public void doUninstallBotAdapter(BotAdapterEntity botAdapterEntity){
        try {
            osgiService.uninstallBotAdapter(botAdapterEntity.getName());
            String summary = botAdapterEntity.getName() + " bot adapter successfully uninstalled";
            FacesMessage mess = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, summary);
            FacesContext.getCurrentInstance().addMessage(null, mess);
        } catch (Exception e) {
            FacesMessage mess = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().getMessage(), ExceptionUtils.getStackTrace(e));
            FacesContext.getCurrentInstance().addMessage(null, mess);
        }
    }
}
