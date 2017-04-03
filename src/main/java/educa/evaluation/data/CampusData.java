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
    private boolean primary;
    private boolean hasChildren;
    private QuestionnaireResults questionnaireResults;
    private List<CampusData> innerCampus;
}
