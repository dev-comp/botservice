package botservice.web.controller.bot.botAdapter;

import botservice.model.bot.BotAdapterEntity;
import botservice.properties.BotServicePropertyConst;
import botservice.service.BotService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.FileInputStream;
import java.io.InputStream;
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

    @Resource(name = BotServicePropertyConst.OSGI_BUNDLE_CONTEXT_JNDI_NAME)
    private BundleContext bundleContext;

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
        return getBundle(botAdapterEntity) != null;
    }

    public void doDeleteBotAdapter(BotAdapterEntity botAdapterEntity){
        botAdapterList.remove(botAdapterEntity);
        botService.removeEntity(botAdapterEntity);
    }

    private Bundle getBundle(BotAdapterEntity botAdapterEntity){
        return bundleContext.getBundle(botAdapterEntity.getName());
    }

    public boolean isInstallBotAdapterDisabled(BotAdapterEntity botAdapterEntity){
        return getBundle(botAdapterEntity) != null;
    }

    public void doInstallBotAdapter(BotAdapterEntity botAdapterEntity){
        try {
            InputStream inputStream = new FileInputStream(botAdapterEntity.getFilePath());
            bundleContext.installBundle(botAdapterEntity.getName(), inputStream);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public boolean isStartBotAdapterDisabled(BotAdapterEntity botAdapterEntity){
        return (getBundle(botAdapterEntity) == null) || (botAdapterEntity.getState() == 1);
    }

    public void doStartBotAdapter(BotAdapterEntity botAdapterEntity){
        try {
            Bundle bundle = getBundle(botAdapterEntity);
            if (bundle != null){
                bundle.start();
                botAdapterEntity.setState(1);
                doSaveBotAdapterEntity(botAdapterEntity);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public boolean isStopBotAdapterDisabled(BotAdapterEntity botAdapterEntity){
        return (getBundle(botAdapterEntity) == null) || (botAdapterEntity.getState() == 0);
    }

    public void doStopBotAdapter(BotAdapterEntity botAdapterEntity){
        try {
            Bundle bundle = getBundle(botAdapterEntity);
            if (bundle != null){
                bundle.stop();
                // todo сменить статус всех entry на 0 (адаптер погасит потоки автоматически)
                botAdapterEntity.setState(0);
                doSaveBotAdapterEntity(botAdapterEntity);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public boolean isUninstallBotAdapterDisabled(BotAdapterEntity botAdapterEntity){
        return (   ((getBundle(botAdapterEntity) != null) && (botAdapterEntity.getState() == 1))
                || getBundle(botAdapterEntity) == null);
    }

    public void doUninstallBotAdapter(BotAdapterEntity botAdapterEntity){
        try {
            Bundle bundle = getBundle(botAdapterEntity);
            if (bundle != null)
                bundle.uninstall();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
