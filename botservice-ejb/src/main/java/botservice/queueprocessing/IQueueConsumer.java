package botservice.queueprocessing;

import com.bftcom.devcomp.api.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Интерфейс обработчика сообщений
 */
public interface IQueueConsumer {
    ObjectMapper mapper = new ObjectMapper();

    void handleMessage(Message message) throws IOException;
}
