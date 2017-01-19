package educa.evaluation.data;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
public class ImprovementQuestion {

    private boolean selected;

    private String id;

    private int priority;

    private double subdimensionWeights;

    private Integer campusPriority;

    private String question;

    private String action;

    private String goal;

    private String responsible;

    private Date startDate;

    private Date endDate;

    private String evidence;

    private String resources;

    private DimensionData.DimensionDataId dimensionId;

    private DimensionData.DimensionDataId subdimensionId;

}
