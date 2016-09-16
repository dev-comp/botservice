package botservice.web.controller.bot.common;

import botservice.properties.BotServicePropertyConst;
import com.bftcom.devcomp.api.IBotManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.annotation.Resource;
import javax.faces.bean.ApplicationScoped;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * Инструменты для работы с OSGI
 */

@ApplicationScoped
public class OSGIService implements Serializable {

    @Resource(name = BotServicePropertyConst.OSGI_BUNDLE_CONTEXT_JNDI_NAME)
    private BundleContext bundleContext;

    private Bundle getBundle(String bundleName){
        return bundleContext.getBundle(bundleName);
    }

    //todo Тут переделать, расширив интерфейс IBotManager и передав в этот метод имя адаптера.
    //todo Кроме этого есть смысл кэшировать все адаптеры при первом обращении и только если не нашли лезть в BundleContext
    private IBotManager getBotManager(){
        return ((IBotManager) bundleContext.getService(bundleContext.getServiceReference(IBotManager.class.getName())));
    }

    public Bundle installBotAdapter(String filePath, String uqName){
        try {
            InputStream inputStream = new FileInputStream(filePath);
            return bundleContext.installBundle(uqName, inputStream);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public boolean uninstallBotAdapter(String uqName){
        try {
            Bundle bundle = getBundle(uqName);
            if (bundle != null){
                bundle.uninstall();
                return true;
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return false;
    }


    public boolean startBotAdapter(String uqName){
        try {
            Bundle bundle = getBundle(uqName);
            if (bundle != null){
                bundle.start();
                return true;
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean stopBotAdapter(String uqName){
        try {
            Bundle bundle = getBundle(uqName);
            if (bundle != null){
                bundle.stop();
                return true;
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return false;
    }



    public boolean isBundleInstalled(String bundleName){
        return getBundle(bundleName) != null;
    }

    public boolean startEntrySession(String uqName, Map<String, String> props) {
        return getBotManager().startBotSession(uqName, props);
    }

    public boolean stopEntrySession(String uqName){
        return getBotManager().stopBotSession(uqName);
    }
}
