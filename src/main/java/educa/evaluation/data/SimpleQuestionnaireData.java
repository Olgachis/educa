package educa.evaluation.data;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
public class SimpleQuestionnaireData {

    private String id;
    private String name;
    private String responseId;
    private Map<String, ?> questionnaire;
    private Long responseCount;
    private boolean active;

}
