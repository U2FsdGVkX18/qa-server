package qa.common;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 钉钉机器人发送消息工具
 */
@Slf4j
public class DingMsgSend {

    private static final String robotWebhook =
            "https://oapi.dingtalk.com/robot/send?access_token=5cf5de5a92793336d212fdada7937a1cf942efc3c46553c5b0efcc383b238396";

    public static void sendMsg(Map<String, Object> msgMap) {
        DingTalkClient client = createClient();
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        //获取消息体
        String msg = msgBody(msgMap);
        //将消息提放入content
        text.setContent(msg);
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();

        //at.setAtMobiles(Arrays.asList("132xxxxxxxx"));
        //isAtAll类型如果不为Boolean，请升级至最新SDK
        at.setIsAtAll(false);

        //at.setAtUserIds(Arrays.asList("109929", "32099"));
        request.setAt(at);
        try {
            OapiRobotSendResponse response = client.execute(request);
            log.debug("日志->>>DingMsgSend:sendMsg->>>成功:{}", response.isSuccess());
        } catch (ApiException e) {
            log.error("日志->>>DingMsgSend:sendMsg->>>失败:{}", e.getMessage());
        }
    }

    public static String msgBody(Map<String, Object> msgMap) {
        String createTime = TimeConvertTool.getDateTime13((Long) msgMap.get("createTime"));
        String updateTime = TimeConvertTool.getDateTime13((Long) msgMap.get("updateTime"));
        String reportcreateTime = TimeConvertTool.getDateTime13((Long) msgMap.get("reportcreateTime"));
        String endTime = TimeConvertTool.getDateTime13((Long) msgMap.get("endTime"));
        msgMap.put("createTime", createTime);
        msgMap.put("updateTime", updateTime);
        msgMap.put("reportcreateTime", reportcreateTime);
        msgMap.put("endTime", endTime);

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
                "任务开始时间: " + msgMap.get("reportcreateTime") + "\n" +
                "任务结束时间: " + msgMap.get("endTime") + "\n" +
                "任务通过率: " + msgMap.get("passRate") + "\n" +
                "任务结果: " + msgMap.get("lastResult") + "\n" +
                "报告url: " + "http://121.41.203.160:8081/#/api/automation/report/view/" + msgMap.get("reportId") + "\n"
                ;
    }

    public static DingTalkClient createClient() {
        return new DefaultDingTalkClient(robotWebhook);
    }
}
