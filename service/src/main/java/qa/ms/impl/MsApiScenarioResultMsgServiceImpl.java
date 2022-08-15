package qa.ms.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
            //线程休眠10000ms
            Thread.sleep(10000);
            //然后去执行数据查询获取场景任务执行结果
            queryResult(scenarioId);
        } catch (InterruptedException e) {
            log.error("日志->>>service:queryResultAsync->>>{}", e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void queryResult(String scenarioId) {
        MsApiScenario msApiScenario = msApiScenarioResultMsgMapper.selectByScenarioId(scenarioId);
        if (ObjectUtils.isEmpty(msApiScenario)) {
            log.info("日志->>>service:queryResult->>>查询结果为空,不发送消息:{}", msApiScenario);
            return;
        }
        //对象转为JSONObject,需要将对象先转为JSONString再转为JSONObject
        String msApiScenarioStr = JSONObject.toJSONString(msApiScenario);
        JSONObject msApiScenarioJson = JSONObject.parseObject(msApiScenarioStr);
        //该key对应value过长且不需要,所以置为空
        msApiScenarioJson.put("scenarioDefinition", "");

        MsApiScenarioReport msApiScenarioReport = msApiScenarioResultMsgMapper.selectByReportId((String) msApiScenarioJson.get("reportId"));
        if (ObjectUtils.isEmpty(msApiScenarioReport)) {
            log.info("日志->>>service:queryResult->>>查询结果为空,不发送消息:{}", msApiScenarioReport);
            return;
        }
        String msApiScenarioReportStr = JSONObject.toJSONString(msApiScenarioReport);
        JSONObject msApiScenarioReportJson = JSONObject.parseObject(msApiScenarioReportStr);

        //msApiScenarioJson和msApiScenarioReportJson有重复key值,为了避免合并后相同的key值覆盖,所以将msApiScenarioReportJson中有重复的key值重命名
        String[] keys = new String[]{"id", "name", "versionId", "projectId", "status", "createTime", "updateTime"};
        //调用mapTool方法处理
        mapTool(msApiScenarioReportJson, keys);

        //将msApiScenarioReport和msApiScenarioReportStr合并
        HashMap<String, Object> newMap = new HashMap<>();
        newMap.putAll(msApiScenarioJson);
        newMap.putAll(msApiScenarioReportJson);
        log.info("日志->>>service:queryResult->>>合并结果:{}", newMap);

        //将新Map对象(消息集合)交给DingMsgSend处理并发送
        DingMsgSend.sendMsg(newMap);
    }

    /**
     * 集合工具,将指定集合中指定的key重命名
     *
     * @param map  指定集合
     * @param keys 指定key
     */
    public void mapTool(Map<String, Object> map, String[] keys) {
        for (String key : keys) {
            Object value = map.get(key);
            map.remove(key);
            map.put("report" + key, value);
        }
    }
}
