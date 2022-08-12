package qa.mock.service;


import qa.mock.TradePayOrder;

import java.util.Map;

public interface TradePayOrderService {

    /**
     * 接收入参appPayOrder并进行处理
     *
     * @param tradePayOrder 传入tradePayOrder对象
     * @return 返回qa订单流水号和状态
     */
    Map<String, Object> execute(TradePayOrder tradePayOrder);

    /**
     * 接收qa订单流水号查询订单状态
     *
     * @param qaOutTradeNo 传入订单流水号对象
     * @return 返回订单状态
     */
    Map<String, Integer> queryStatus(String qaOutTradeNo);

}
