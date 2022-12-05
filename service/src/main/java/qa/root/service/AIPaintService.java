package qa.root.service;


import qa.root.AIPaint;

import java.util.Map;

public interface AIPaintService {

    /**
     * 执行任务
     *
     * @param aiPaint 接口参数
     * @return 响应结果
     */
    Map<String, Object> execute(AIPaint aiPaint);
}
