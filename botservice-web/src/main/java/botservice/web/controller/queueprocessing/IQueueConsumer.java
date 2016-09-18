package botservice.web.controller.queueprocessing;

import com.bftcom.devcomp.bots.Message;

import java.io.IOException;

/**
 * Интерфейс обработчика сообщений
 */
public interface IQueueConsumer {

    void handleMessage(Message message) throws IOException;
}
