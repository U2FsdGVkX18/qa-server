package qa.mapper.ms;

import org.apache.ibatis.annotations.Mapper;
import qa.ms.MsApiScenario;
import qa.ms.MsApiScenarioReport;

@Mapper
public interface MsApiScenarioResultMsgMapper {

    /**
     * 根据场景id查询场景数据
     *
     * @param scenarioId 场景id
     * @return 返回一个MsApiScenario对象
     */
    MsApiScenario selectByScenarioId(String scenarioId);

    /**
     * 根据报告id查询报告数据
     *
     * @param reportId 报告id
     * @return 返回一个MsApiScenarioReport对象
     */
    MsApiScenarioReport selectByReportId(String reportId);

}
