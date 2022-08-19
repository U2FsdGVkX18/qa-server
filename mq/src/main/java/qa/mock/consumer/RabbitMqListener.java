package qa.mock.consumer;


import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import qa.common.RestTemplateTool;
import qa.mock.RabbitMqCreateMsg;

import javax.annotation.Resource;

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

        RestTemplateTool restTemplateTool = new RestTemplateTool();
        String baseUrl = "https://restapi.amap.com/v3/geocode/geo";
        String key = "c17ba5b2f9277ed0326ab4fa6019b0cf";
        String url = baseUrl + "?key=" + key + "&address=" + "五常街道" + "&output=JSON";

        try {
            ResponseEntity<String> stringResponseEntity = restTemplateTool.sendGetRequest(url, new JSONObject());
            log.info("日志->>>RabbitMqListener->>>三方接口返回数据:{}", stringResponseEntity);
            //做回调业务操作
            //if满足条件则接收消息,业务操作成功并结束
            if (stringResponseEntity.getStatusCodeValue() == 200) {
                channel.basicAck(deliveryTag, true);
                log.info("日志->>>RabbitMqListener->>>消费者确认接收到消息 : [{}]", msg);
            } else {
                //不满足条件则说明业务操作不通过,重新发送消息
                //首先把当前消息拒绝
                channel.basicReject(deliveryTag, false);
                log.info("日志->>>RabbitMqListener->>>拒绝当前消息 : [{}]", msg);
                //然后重新发送消息
                Message newMessage = RabbitMqCreateMsg.CreateMsg(msg, "10000");
                rabbitTemplate.convertAndSend("pay.message.exchange", "payMessage", newMessage);
                log.info("日志->>>RabbitMqListener->>>重新发送消息 : [{}]", msg);
            }
        } catch (Exception e) {
            log.error("日志->>>RabbitMqListener->>>异常 : [{}]", e.getMessage());
        }
    }
}
