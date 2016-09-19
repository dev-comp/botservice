package botservice.queueprocessing;

import com.bftcom.devcomp.bots.BotCommand;
import com.bftcom.devcomp.bots.IBotConst;
import com.bftcom.devcomp.bots.Message;
import com.rabbitmq.client.Channel;

import java.io.IOException;

/**
 * Потребитель сообщений от конкретных экземпляров ботов
 */

public class EntryQueueConsumer extends CommonQueueConsumer {

    public EntryQueueConsumer(Channel channel) {
        super(channel);
    }

    @SuppressWarnings("unused")
    IQueueConsumer entryProcessMessageConsumer = new AbstractQueueConsumer(BotCommand.SERVICE_PROCESS_ENTRY_MESSAGE) {
        @Override
        public void handleMessage(Message message) throws IOException {
            System.err.println(message.getUserProperties().get(IBotConst.PROP_BODY_TEXT));
            //todo реализация метода проброски сообщений клиентским приложениям
        }
    };

}
