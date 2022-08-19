package qa.mock;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;

import java.nio.charset.StandardCharsets;

/**
 * 创建消息对象
 */
public class RabbitMqCreateMsg {

    /**
     * 创建消息主体
     *
     * @param msgBody        消息内容
     * @param expirationTime 过期时间
     * @return Message
     */
    public static Message CreateMsg(String msgBody, String expirationTime) {
        return MessageBuilder
                .withBody(msgBody.getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT)   //设置持久化或非,目前设置非持久化
                .setExpiration(expirationTime)    //设置超时时间,单位:毫秒ms    (如果队列和消息都设置了超时时间 那么以时间较短的为准) 目前只设置了消息超时时间
                .build();
    }
}
