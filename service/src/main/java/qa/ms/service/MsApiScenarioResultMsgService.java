package qa.ms.service;

import org.springframework.scheduling.annotation.Async;


public interface MsApiScenarioResultMsgService {

    /**
     * 异步任务
     * 根据场景id查询数据后在service层处理,传给钉钉发送消息;不用返回给controller层
     *
     * @param scenarioId 场景ID
     */
    @Async
    void queryResultAsync(String scenarioId);
}
