package qa.ms;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import qa.common.CommonResult;
import qa.ms.service.MsApiScenarioResultMsgService;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/ms")
public class MsApiScenarioResultMsgController {

    @Resource
    private MsApiScenarioResultMsgService msApiScenarioResultMsgService;

    /**
     * 定义get接口发送场景id给service层处理即可,本接口不用返回数据
     *
     * @param scenarioId 场景ID
     * @return 返回一个字符串
     */
    @GetMapping("/api/sendResultMsg")
    public CommonResult<String> sendResultMsg(@RequestParam String scenarioId, @RequestParam String robotToken) {
        log.info("日志->>>controller:payOrderQuery->>>接口接收到的参数:{},{}", scenarioId, robotToken);
        try {
            //调用service层异步任务后,不用等待;接口立即返回
            msApiScenarioResultMsgService.queryResultAsync(scenarioId, robotToken);
            return CommonResult.success("success");
        } catch (Exception e) {
            log.error("日志->>>controller:payOrderQuery->>>调用service失败:{}", e.getMessage());
            return CommonResult.failed("fail");
        }
    }
}
