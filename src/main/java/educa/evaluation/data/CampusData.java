package educa.evaluation.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampusData {
    private String id;
    private String name;
    private String campusType;
    private boolean primary;
    private QuestionnaireResults questionnaireResults;
}
