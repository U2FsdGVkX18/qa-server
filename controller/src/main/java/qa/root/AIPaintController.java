package qa.root;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qa.common.CommonResult;
import qa.root.service.AIPaintService;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/root")
public class AIPaintController {
    @Resource
    private AIPaintService aiPaintService;

    @PostMapping("/api/aiModelResult")
    public CommonResult<Map<String, Object>> aiModel(@RequestBody AIPaint aiPaint) {
        log.info("日志->>>controller:aiModel->>>接口接收到的参数:{}", aiPaint);
        try {
            Map<String, Object> responseMap = aiPaintService.execute(aiPaint);
            return CommonResult.success(responseMap);
        } catch (Exception e) {
            log.error("日志->>>controller:aiModel->>>调用service失败:{}", e.getMessage());
            return CommonResult.validateFailed();
        }
    }
}
