package qa.mock;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 定义队列和交换机并创建绑定关系
 */
@Configuration
public class RabbitMqDeclareConfig {

    //支付通知交换机
    private final String DIRECT_EXCHANGE_NAME = "pay.message.exchange";
    //支付通知队列
    private final String QUEUE_NAME = "pay.message.queue";
    //支付通知死信交换机
    private final String DLX_EXCHANGE_NAME = "pay.message.dlx.exchange";
    //支付通知死信队列
    private final String DLX_QUEUE_NAME = "pay.message.dlx.queue";

    /**
     * 声明支付通知交换机
     */
    @Bean
    public DirectExchange payMessageExchange() {
        return new DirectExchange(DIRECT_EXCHANGE_NAME);
    }

    /**
     * 声明支付通知队列
     * 注意:需要指定死信交换机和死信routingKey(间接绑定)
     */
    @Bean
    public Queue payMessageQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .deadLetterExchange(DLX_EXCHANGE_NAME)
                .deadLetterRoutingKey("payMessageDlx")
                .build();
    }

    /**
     * 将支付通知交换机和支付通知队列进行绑定
     */
    @Bean
    public Binding payMessageBinding() {
        return BindingBuilder
                .bind(payMessageQueue())
                .to(payMessageExchange())
                .with("payMessage");
    }

    /**
     * 声明支付死信交换机
     */
    @Bean
    public DirectExchange payMessageExchangeDlx() {
        return new DirectExchange(DLX_EXCHANGE_NAME);
    }

    /**
     * 声明支付死信队列
     * 略:(给队列设置过期时间,那么其中的所有消息都将按照过期时间过期)
     */
    @Bean
    public Queue payMessageQueueDlx() {
        return new Queue(DLX_QUEUE_NAME);
//        return QueueBuilder.durable(DLX_QUEUE_NAME).build();
    }

    /**
     * 将支付通知死信交换机和支付通知死信队列进行绑定
     */
    @Bean
    public Binding payMessageDlxBinding() {
        return BindingBuilder
                .bind(payMessageQueueDlx())
                .to(payMessageExchangeDlx())
                .with("payMessageDlx");
    }

    /**
     @RabbitListener(bindings = @QueueBinding(
     value = @Queue(name = "topic_queue"),
     exchange = @Exchange(name = "topic", type = ExchangeTypes.TOPIC),
     key = "china.#"
     ))
     public void listenTopicQueue() {
     System.out.println("消费者接收到消息");
     }
     */
}
