package educa.evaluation.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubdimensionResults {

    private DimensionData.DimensionDataId id;

    private float maxQuestions;
    private float maxPoints;
    private float questions;
    private float points;

    private int sortOrder;


}
