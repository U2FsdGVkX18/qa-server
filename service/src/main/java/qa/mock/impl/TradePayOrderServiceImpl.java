package qa.mock.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import qa.mapper.mock.TradePayOrderMapper;
import qa.mock.RabbitMqCreateMsg;
import qa.mock.TradePayOrder;
import qa.mock.service.TradePayOrderService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class TradePayOrderServiceImpl implements TradePayOrderService {

    @Resource
    private TradePayOrderMapper tradePayOrderMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public Map<String, Object> execute(TradePayOrder tradePayOrder) {
        String uuidStr = UUID.randomUUID().toString();
        String uuid = uuidStr.replace("-", "");


        //创建消息对象
        Message message = RabbitMqCreateMsg.CreateMsg(uuid, "0");

        try {

            //发送消息
            rabbitTemplate.convertAndSend("pay.message.exchange", "payMessage", message);
            log.debug("日志->>>service:execute->>>mq消息发送成功,uuid:{}", uuid);
        } catch (AmqpException e) {
            log.error("日志->>>service:execute->>>mq消息发送失败:{}", e.getMessage());
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("qaOutTradeNo", uuid);

        Random random = new Random();
        hashMap.put("status", random.nextInt(2));

        tradePayOrder.setQaOutTradeNo((String) hashMap.get("qaOutTradeNo"));
        tradePayOrder.setStatus((Integer) hashMap.get("status"));

        int isSuccess = tradePayOrderMapper.insert(tradePayOrder);
        log.debug("日志->>>service:execute->>>insert是否插入成功:{}", isSuccess);
        if (isSuccess > 0) {
            return hashMap;
        }
        return null;
    }

    @Override
    public Map<String, Integer> queryStatus(String qaOutTradeNo) {
        TradePayOrder tradePayOrder = tradePayOrderMapper.selectByUUID(qaOutTradeNo);
        if (tradePayOrder == null) {
            log.debug("日志->>>service:queryStatus->>>查询结果为空");
            return null;
        }
        log.debug("日志->>>service:queryStatus->>>查询结果为:{}", tradePayOrder);
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("status", tradePayOrder.getStatus());
        return hashMap;
    }
}
