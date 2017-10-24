package educa.evaluation.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CampusData {
    private String id;
    private String name;
    private String campusType;
    private String campusName;
    private boolean primary;
    private QuestionnaireResults questionnaireResults;
    private List<CampusData> innerCampus;
}
