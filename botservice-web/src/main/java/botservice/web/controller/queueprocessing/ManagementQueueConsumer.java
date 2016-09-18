package botservice.web.controller.queueprocessing;

import com.bftcom.devcomp.bots.BotCommand;
import com.bftcom.devcomp.bots.Message;
import com.rabbitmq.client.Channel;

import java.io.IOException;

/**
 * Потребитель управляющих сообщений
 */

public class ManagementQueueConsumer extends CommonQueueConsumer {

    public ManagementQueueConsumer(Channel channel) {
        super(channel);
    }

    @SuppressWarnings("unused")
    IQueueConsumer adapterProcessMessageConsumer = new AbstractQueueConsumer(BotCommand.ADAPTER_PROCESS_MESSAGE) {
        @Override
        public void handleMessage(Message message) throws IOException {
            //todo реализация метода обработки служебного сообщения
        }
    };


}
