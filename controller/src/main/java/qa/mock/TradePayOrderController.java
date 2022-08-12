package qa.mock;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import qa.common.CommonResult;
import qa.mock.service.TradePayOrderService;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/mock")
public class TradePayOrderController {
    @Resource
    private TradePayOrderService tradePayOrderService;

    @PostMapping("/api/order/payOrder")
    public CommonResult<Map<String, Object>> payOrder(@RequestBody TradePayOrder tradePayOrder) {
        log.info("日志->>>controller:payOrder->>>接口接收到的参数:{}", tradePayOrder);
        if (StringUtils.isEmpty(tradePayOrder.getNotifyUrl())) {
            return CommonResult.validateFailed();
        }
        Map<String, Object> responseMap = tradePayOrderService.execute(tradePayOrder);
        log.info("日志->>>controller:payOrder->>>接口响应数据:{}", responseMap);
        return CommonResult.success(responseMap);
    }

    @GetMapping("/api/order/payOrderQuery/{qaOutTradeNo}")
    public CommonResult<Map<String, Integer>> payOrderQuery(@PathVariable String qaOutTradeNo) {
        log.info("日志->>>controller:payOrderQuery->>>接口接收到的参数:{}", qaOutTradeNo);
        if (StringUtils.isEmpty(qaOutTradeNo)) {
            return CommonResult.validateFailed();
        }
        Map<String, Integer> responseMap = tradePayOrderService.queryStatus(qaOutTradeNo);
        log.info("日志->>>controller:payOrderQuery->>>接口响应数据:{}", responseMap);
        return CommonResult.success(responseMap);
    }
}
