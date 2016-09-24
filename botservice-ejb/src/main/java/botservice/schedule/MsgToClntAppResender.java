package botservice.schedule;

import botservice.properties.BotServiceProperty;
import botservice.properties.BotServicePropertyConst;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import java.io.Serializable;

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
            //todo тут пробовать отослать сообщения клиентским приложениям
        }
    }
}
