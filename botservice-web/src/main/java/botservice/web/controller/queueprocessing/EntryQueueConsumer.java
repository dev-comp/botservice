package botservice.web.controller.queueprocessing;

import com.bftcom.devcomp.bots.Message;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Потребитель сообщений от конкретных экземпляров ботов
 */

public class EntryQueueConsumer extends DefaultConsumer {

    public EntryQueueConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String sMsg = new String(body, StandardCharsets.UTF_8);
        Message message = BotManager.mapper.readValue(sMsg, Message.class);
        // todo допилить обработку сообщений от экземпляров ботов
    }
}
