package botservice.web.controller.common;

import botservice.properties.BotServicePropertyConst;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.annotation.Resource;
import javax.faces.bean.ApplicationScoped;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;

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
}
