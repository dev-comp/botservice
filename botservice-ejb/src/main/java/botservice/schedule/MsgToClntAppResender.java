package botservice.schedule;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.TimerService;

/**
 * Бин, вызываемый по расписанию и пытающийся доставить те сообщения клиентским приложениям,
 * которые по каким-либо причинам не удалось доставить сразу
 */

@Singleton
public class MsgToClntAppResender {

    @Resource
    private TimerService timerService;
}
