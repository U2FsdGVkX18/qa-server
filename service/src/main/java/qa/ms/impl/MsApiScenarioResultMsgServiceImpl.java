package qa.ms.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import qa.common.DingMsgSend;
import qa.mapper.ms.MsApiScenarioResultMsgMapper;
import qa.ms.MsApiScenario;
import qa.ms.MsApiScenarioReport;
import qa.ms.service.MsApiScenarioResultMsgService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class MsApiScenarioResultMsgServiceImpl implements MsApiScenarioResultMsgService {
    @Resource
    private MsApiScenarioResultMsgMapper msApiScenarioResultMsgMapper;

    @Override
    public void queryResultAsync(String scenarioId) {
        try {
            Thread.sleep(10000);
            queryResult(scenarioId);
        } catch (InterruptedException e) {
            log.error("日志->>>service:queryResultAsync->>>{}", e.getMessage());
        }
    }

    public void queryResult(String scenarioId) {
        MsApiScenario msApiScenario = msApiScenarioResultMsgMapper.selectByScenarioId(scenarioId);
        if (ObjectUtils.isEmpty(msApiScenario)) {
            log.debug("日志->>>service:queryResult->>>查询结果为空,不发送消息:{}", msApiScenario);
            return;
        }
        String msApiScenarioStr = JSONObject.toJSONString(msApiScenario);
        JSONObject msApiScenarioJson = JSONObject.parseObject(msApiScenarioStr);
        msApiScenarioJson.put("scenarioDefinition", "");

        MsApiScenarioReport msApiScenarioReport = msApiScenarioResultMsgMapper.selectByReportId((String) msApiScenarioJson.get("reportId"));
        String msApiScenarioReportStr = JSONObject.toJSONString(msApiScenarioReport);
        JSONObject msApiScenarioReportJson = JSONObject.parseObject(msApiScenarioReportStr);

        String[] keys = new String[]{"id", "name", "versionId", "projectId", "status", "createTime", "updateTime"};
        mapTool(msApiScenarioReportJson, keys);

        HashMap<String, Object> newMap = new HashMap<>();
        newMap.putAll(msApiScenarioJson);
        newMap.putAll(msApiScenarioReportJson);


        //钉钉机器人发送消息
        DingMsgSend.sendMsg(newMap);
        log.debug("日志->>>service:queryResult->>>查询结果:{}", newMap);
    }


    public void mapTool(Map<String, Object> map, String[] keys) {
        for (String key : keys) {
            Object value = map.get(key);
            map.remove(key);
            map.put("report" + key, value);
        }
    }

}
