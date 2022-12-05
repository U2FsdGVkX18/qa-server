package qa.root;

import lombok.Data;

import java.io.Serializable;

@Data
public class DiscoPicModel implements Serializable {

    private static final long serialVersionUID = 6104655296110365473L;

    private String modelName;

    private Integer aiPicId;

    private String batchName;

    private String hashServerUrl;

    private Integer height;

    private Integer width;

    private String textPrompt;
    
    private Integer multiple;

    private Integer strength;

    private Integer steps;

    private Integer scale;
}
