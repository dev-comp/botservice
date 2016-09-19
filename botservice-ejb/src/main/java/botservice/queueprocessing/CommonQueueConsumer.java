package botservice.queueprocessing;

import com.bftcom.devcomp.bots.BotCommand;
import com.bftcom.devcomp.bots.Message;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс-предок для обработчиков сообщений
 */

public class CommonQueueConsumer extends DefaultConsumer {

    private Map<Enum<BotCommand>, IQueueConsumer> consumerMap = new HashMap<>();

    public CommonQueueConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String sMsg = new String(body, StandardCharsets.UTF_8);
        Message message = IQueueConsumer.mapper.readValue(sMsg, Message.class);
        IQueueConsumer queueConsumer = consumerMap.get(message.getCommand());
        if (queueConsumer == null)
            throw new RuntimeException("Consumer not found");
        queueConsumer.handleMessage(message);
    }

    protected abstract class AbstractQueueConsumer implements IQueueConsumer{

        protected AbstractQueueConsumer(BotCommand botCommand){
            consumerMap.put(botCommand, this);
        }
    }

}
