package qa.ms.service;

import org.springframework.scheduling.annotation.Async;


public interface MsApiScenarioResultMsgService {

    /**
     * 异步任务
     * 根据场景id在service层查询并处理数据,交给DingMsgSend处理并发送;不用返回给controller层
     *
     * @param scenarioId 场景ID
     */
    @Async
    void queryResultAsync(String scenarioId);
}
