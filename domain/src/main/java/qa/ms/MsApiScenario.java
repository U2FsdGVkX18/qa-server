package qa.ms;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "api_scenario")
public class MsApiScenario implements Serializable {

    private static final long serialVersionUID = -5169039706298408416L;

    private String id;

    private String projectId;

    private String tags;

    private String userId;

    private String apiScenarioModuleId;

    private String modulePath;

    private String name;

    private String level;

    private String status;

    private String principal;

    private Integer stepTotal;

    private String schedule;

    private String scenarioDefinition;

    private String description;

    private Long createTime;

    private Long updateTime;

    private String passRate;

    private String lastResult;

    private String reportId;

    private Integer num;

    private String originalState;

    private String customNum;

    private String createUser;

    private Integer version;

    private Long deleteTime;

    private String deleteUserId;

    private Integer executeTimes;

    private Long order;

    private String environmentType;

    private String environmentJson;

    private String environmentGroupId;

    private String versionId;

    private String refId;

    private Integer latest;

}
