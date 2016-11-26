package educa.evaluation.data;

import lombok.Data;

import java.util.List;

@Data
public class SubDimensionData {

    private final DimensionData.DimensionDataId id;
    private final List<Question> questions;
    private final String comment;

}
