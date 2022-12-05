package qa.root;

import lombok.Data;

import java.io.Serializable;

@Data
public class AIPaint implements Serializable {

    private static final long serialVersionUID = -7931798349677632211L;

    //机器ID
    private Integer agentId;

    //环境ID
    private Integer snapId;

    //镜像ID
    private Integer imageId;

    //账号权限
    private String authorization;

    //generate_disco_pic接口参数
    private DiscoPicModel discoPicModel;

}
