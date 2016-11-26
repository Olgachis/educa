package educa.evaluation.data;

import lombok.Data;

import java.util.Map;

@Data
public class QuestionnaireData {

    private final Map<String, DimensionData> dimensions;

}
