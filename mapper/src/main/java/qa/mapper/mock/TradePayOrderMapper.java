package qa.mapper.mock;

import org.apache.ibatis.annotations.Mapper;
import qa.mock.TradePayOrder;

@Mapper
public interface TradePayOrderMapper {

    /**
     * 插入支付订单数据
     *
     * @param tradePayOrder 传入tradePayOrder对象,执行插入操作
     * @return 返回sql执行状态
     */
    int insert(TradePayOrder tradePayOrder);

    /**
     * 通过uuid查询订单数据
     *
     * @param qaOutTradeNo 传入qaOutTradeNo
     * @return 返回TradePayOrder对象
     */
    TradePayOrder selectByUUID(String qaOutTradeNo);

}
