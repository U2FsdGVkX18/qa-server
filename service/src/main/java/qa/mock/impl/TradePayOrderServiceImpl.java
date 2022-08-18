package qa.mock.impl;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qa.common.UUIDTool;
import qa.mapper.mock.TradePayOrderMapper;
import qa.mock.RabbitMqCreateMsg;
import qa.mock.TradePayOrder;
import qa.mock.service.TradePayOrderService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class TradePayOrderServiceImpl implements TradePayOrderService {

    @Resource
    private TradePayOrderMapper tradePayOrderMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> execute(TradePayOrder tradePayOrder) {
        //获取一个uuid
        String uuid = UUIDTool.generateStrWithoutHyphen();
        //创建一个random对象
        Random random = new Random();

        //组装map,用于响应
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("qaOutTradeNo", uuid);

        hashMap.put("status", random.nextInt(2));

        //组装对象,用于插库
        tradePayOrder.setQaOutTradeNo((String) hashMap.get("qaOutTradeNo"));
        tradePayOrder.setStatus((Integer) hashMap.get("status"));

        int isSuccess = tradePayOrderMapper.insert(tradePayOrder);

        //判断记录是否插入成功
        if (isSuccess > 0) {
            log.info("日志->>>service:execute->>>insert插入成功:{}", isSuccess);

            //创建消息对象 uuid:发送的消息,消息过期时间
            Message message = RabbitMqCreateMsg.CreateMsg(uuid, "10000");
            try {
                //发送消息 交换机,路由键,消息
                rabbitTemplate.convertAndSend("pay.message.exchange", "payMessage", message);
                log.info("日志->>>service:execute->>>mq消息发送成功,uuid:{}", uuid);
            } catch (AmqpException e) {
                log.error("日志->>>service:execute->>>mq消息发送失败:{}", e.getMessage());
            }
            return hashMap;
        }
        //记录没有插入成功,则返回
        log.error("日志->>>service:execute->>>insert插入失败,mq消息不发送");
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Integer> queryStatus(String qaOutTradeNo) {
        //库中查询订单状态
        TradePayOrder tradePayOrder = tradePayOrderMapper.selectByUUID(qaOutTradeNo);
        if (ObjectUtils.isEmpty(tradePayOrder)) {
            log.info("日志->>>service:queryStatus->>>查询结果为空:{}", tradePayOrder);
            return null;
        }
        log.info("日志->>>service:queryStatus->>>查询结果为:{}", tradePayOrder);
        //组装map,用于响应
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("status", tradePayOrder.getStatus());
        return hashMap;
    }
}
