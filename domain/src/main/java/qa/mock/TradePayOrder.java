package qa.mock;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "trade_pay_order")
public class TradePayOrder implements Serializable {

    private static final long serialVersionUID = 7823214269868843699L;
    /**
     * 主键ID
     */
    private Long id;

    /**
     * return_url
     */
    private String returnUrl;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 总金额
     */
    private String totalFree;

    /**
     * 订单流水号
     */
    private String outTradeNo;

    /**
     * 开始时间
     */
    private Long timeStart;

    /**
     * 过期时间
     */
    private Long timeExpire;

    /**
     * qa服务 返回三方订单流水号
     */
    private String qaOutTradeNo;

    /**
     * qa服务 自定义订单状态
     */
    private Integer status;

}
