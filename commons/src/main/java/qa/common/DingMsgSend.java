package qa.common;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 封装钉钉机器人发送消息工具
 */
@Slf4j
public class DingMsgSend {

    /**
     * 测试团队群-接口自动化机器人-webhook地址
     */
    private static final String robotWebhook =
            "https://oapi.dingtalk.com/robot/send?access_token=";

    /**
     * 发送消息主体
     *
     * @param msgMap 消息主体
     */
    public static void sendMsg(Map<String, Object> msgMap, String robotToken) {
        //新建请求对象
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        //指定发送消息类型
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        //将消息体放入content
        text.setContent(msgBody(msgMap));
        //将text放入请求对象中
        request.setText(text);
        //设置消息在群中需要@对象
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        //at.setAtMobiles(Arrays.asList("132xxxxxxxx"));
        //isAtAll类型如果不为Boolean，请升级至最新SDK
        //IsAtAll为true:@所有人,IsAtAll为false:不@人
        at.setIsAtAll(false);
        //at.setAtUserIds(Arrays.asList("109929", "32099"));
        request.setAt(at);
        try {
            OapiRobotSendResponse response = createClient(robotToken).execute(request);
            log.info("日志->>>DingMsgSend:sendMsg->>>成功:{}", response.isSuccess());
        } catch (ApiException e) {
            log.error("日志->>>DingMsgSend:sendMsg->>>失败:{}", e.getMessage());
        }
    }

    /**
     * 消息主体构建+格式化
     *
     * @param msgMap 消息集合
     * @return String
     */
    private static String msgBody(Map<String, Object> msgMap) {
        //消息发送出去时间展示不能是时间戳格式的,这里进行时间格式换,时间戳->日期时间型
        msgMap.put("createTime", TimeConvertTool.getDateTime13((Long) msgMap.get("createTime")));
        msgMap.put("updateTime", TimeConvertTool.getDateTime13((Long) msgMap.get("updateTime")));
        msgMap.put("reportcreateTime", TimeConvertTool.getDateTime13((Long) msgMap.get("reportcreateTime")));
        msgMap.put("endTime", TimeConvertTool.getDateTime13((Long) msgMap.get("endTime")));

        return "消息通知:\n" +
                "任务执行结果:\n" +
                "\n" +
                "场景名称: " + msgMap.get("name") + "\n" +
                "场景ID: " + msgMap.get("id") + "\n" +
                "责任人: " + msgMap.get("principal") + "\n" +
                "模块路径: " + msgMap.get("modulePath") + "\n" +
                "级别: " + msgMap.get("level") + "\n" +
                "步骤总数: " + msgMap.get("stepTotal") + "\n" +
                "执行次数: " + msgMap.get("executeTimes") + "\n" +
                "场景创建时间: " + msgMap.get("createTime") + "\n" +
                "场景更新时间: " + msgMap.get("updateTime") + "\n" +
                "\n" +
                "任务名称: " + msgMap.get("reportname") + "\n" +
                "任务触发方式: " + msgMap.get("triggerMode") + "\n" +
                "任务开始时间: " + msgMap.get("reportcreateTime") + "\n" +
                "任务结束时间: " + msgMap.get("endTime") + "\n" +
                "任务通过率: " + msgMap.get("passRate") + "\n" +
                "任务结果: " + msgMap.get("lastResult") + "\n" +
                "报告url: " + "http://121.41.203.160:8081/#/api/automation/report/view/" + msgMap.get("reportId") + "\n"
                ;
    }

    /**
     * 新建钉钉消息客户端对象
     *
     * @return DingTalkClient
     */
    private static DingTalkClient createClient(String robotToken) {
        return new DefaultDingTalkClient(robotWebhook + robotToken);
    }
}
