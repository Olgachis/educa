package educa.evaluation.data;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class QuestionnaireData {

    private String institutionName;
    private String institutionType;

    private boolean internship;
    private boolean initialEducation;
    private boolean preschool;
    private boolean basic;
    private boolean secondary;
    private boolean highSchool;

    private final Map<String, DimensionData> dimensions;

}
