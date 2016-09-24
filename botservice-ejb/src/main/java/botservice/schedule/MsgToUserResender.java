package botservice.schedule;

import botservice.model.system.UserLogEntity;
import botservice.model.system.UserLogEntity_;
import botservice.properties.BotServiceProperty;
import botservice.properties.BotServicePropertyConst;
import botservice.service.SystemService;
import botservice.service.common.BaseParam;
import botservice.serviceException.ServiceException;
import botservice.serviceException.ServiceExceptionObject;
import botservice.util.BotMsgDirectionType;
import botservice.util.BotMsgTransportStatus;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Бин, вызываемый по расписанию и пытающийся доставить те сообщения в очередь ботам (конечным пользователям),
 * которые по каким-либо причинам не удалось доставить сразу
 */

@Singleton
@Startup
public class MsgToUserResender implements Serializable {

    @Resource
    private TimerService timerService;

    @Inject
    SystemService systemService;

    @Inject
    @ServiceException
    Event<ServiceExceptionObject> serviceExceptionEvent;

    @Inject
    @BotServiceProperty(name = BotServicePropertyConst.MSG_TO_USER_RESEND_TIMEAUT)
    private int msgToUserResendTimeOut;

    @PostConstruct
    public void init(){
        timerService.createIntervalTimer(0L, msgToUserResendTimeOut*1000,
                new TimerConfig(this, false));
    }

    @Timeout
    public void resendMsgToClntApp(Timer timer){
        if (timer.getInfo() instanceof MsgToUserResender){
            List<UserLogEntity> userLogEntityList = systemService.getEntityListByCriteria(UserLogEntity.class,
                    new BaseParam(UserLogEntity_.directionType, BotMsgDirectionType.TO_USER),
                    new BaseParam(UserLogEntity_.transportStatus, BotMsgTransportStatus.DEFERRED));
            for(UserLogEntity userLogEntity: userLogEntityList){
                try {
                    systemService.sendMessageToBotQueue(userLogEntity.getMsgBody(), userLogEntity.getUserKeyEntity());
                    userLogEntity.setTransportStatus(BotMsgTransportStatus.DELIVERED);
                    systemService.mergeEntity(userLogEntity);
                } catch (Exception e){
                    serviceExceptionEvent.fire(new ServiceExceptionObject(
                            "Ошибка при попытке повторной отправки сообщения в очередь бота: " +
                                    userLogEntity.getUserKeyEntity().getBotEntity().getName(), e));
                }
            }
        }
    }

}
