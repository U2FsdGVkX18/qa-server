package qa.mock.consumer;


import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import qa.mock.RabbitMqCreateMsg;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * mq消费者
 */
@Component
@Slf4j
public class RabbitMqListener {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @RabbitHandler
    @RabbitListener(queues = "pay.message.dlx.queue")
    public void listenPayMessageDlxQueue(Message message, Channel channel, String msg) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        log.info("日志->>>RabbitMqListener->>>message : [{}]", message);
        try {
            //做回调业务操作

            //if满足条件则接收消息,业务操作成功并结束
            if (msg.startsWith("e")) {
                channel.basicAck(deliveryTag, true);
                log.info("日志->>>RabbitMqListener->>>消费者接收到消息 : [{}]", msg);
            } else {   //不满足条件则说明业务操作不通过,重新发送消息
                //首先把当前消息拒绝
                channel.basicReject(deliveryTag, false);
                log.info("日志->>>RabbitMqListener->>>拒绝当前消息 : [{}]", msg);
                //然后重新发送消息
                Message newMessage = RabbitMqCreateMsg.CreateMsg(msg, "10000");
                rabbitTemplate.convertAndSend("pay.message.exchange", "payMessage", newMessage);
                log.info("日志->>>RabbitMqListener->>>重新发送消息 : [{}]", msg);
            }
        } catch (IOException e) {
            log.error("日志->>>RabbitMqListener->>>异常 : [{}]", e.getMessage());
        }
    }
}
