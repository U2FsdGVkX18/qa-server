package qa.ms;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "api_scenario_report")
public class MsApiScenarioReport implements Serializable {
    private static final long serialVersionUID = 6299246787997746111L;

    private String id;

    private String projectId;

    private String name;

    private String description;

    private Long createTime;

    private Long updateTime;

    private String status;

    private String user_id;

    private String triggerMode;

    private String executeType;

    private String scenarioName;

    private String scenarioId;

    private String create_user;

    private String actuator;

    private Long endTime;

    private Integer reportVersion;

    private String versionId;

    private String reportType;
}
