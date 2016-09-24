package botservice.schedule;

import botservice.model.system.UserLogEntity;
import botservice.model.system.UserLogEntity_;
import botservice.properties.BotServiceProperty;
import botservice.properties.BotServicePropertyConst;
import botservice.queueprocessing.BotManagerService;
import botservice.rest.model.MsgObject;
import botservice.service.SystemService;
import botservice.service.common.BaseParam;
import botservice.serviceException.ServiceException;
import botservice.serviceException.ServiceExceptionObject;
import botservice.util.BotMsgTransportStatus;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Бин, вызываемый по расписанию и пытающийся доставить те сообщения клиентским приложениям,
 * которые по каким-либо причинам не удалось доставить сразу
 */

@Singleton
@Startup
public class MsgToClntAppResender implements Serializable{

    @Resource
    private TimerService timerService;

    @Inject
    BotManagerService botManagerService;

    @Inject
    SystemService systemService;

    @Inject
    @ServiceException
    Event<ServiceExceptionObject> serviceExceptionEvent;

    @Inject
    @BotServiceProperty(name = BotServicePropertyConst.MSG_TO_CLNTAPP_RESEND_TIMEAUT)
    private int msgToClntAppResendTimeOut;

    @PostConstruct
    public void init(){
        timerService.createIntervalTimer(0L, msgToClntAppResendTimeOut*1000,
                new TimerConfig(this, true));
    }

    @Timeout
    public void resendMsgToClntApp(Timer timer){
        if (timer.getInfo() instanceof MsgToClntAppResender){
            List<UserLogEntity> userLogEntityList = systemService.getEntityListByCriteria(UserLogEntity.class,
                    new BaseParam(UserLogEntity_.transportStatus, BotMsgTransportStatus.DEFERRED));
            for(UserLogEntity userLogEntity: userLogEntityList){
                try {
                    MsgObject responseMsgObject =  systemService.sendMsgToClntApp(userLogEntity.getUserKeyEntity().getUserName(),
                            userLogEntity.getMsgBody(), userLogEntity.getUserKeyEntity().getBotEntity());
                    botManagerService.sendMessageToBot(responseMsgObject);
                    userLogEntity.setTransportStatus(BotMsgTransportStatus.DELIVERED);
                    systemService.mergeEntity(userLogEntity);
                } catch (Exception e){
                    serviceExceptionEvent.fire(new ServiceExceptionObject(
                            "Ошибка при попытке повторной отправки сообщения. URL: " +
                                    userLogEntity.getUserKeyEntity().getBotEntity().getClientAppEntity().getPath(), e));
                }
            }
        }
    }
}
