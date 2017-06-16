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
    private Boolean active;
    private String titleFunc;

    @Override
    public String toString() {
        return "SimpleQuestionnaireData{" +
          "responseId='" + responseId + '\'' +
          ", id='" + id + '\'' +
          ", name='" + name + '\'' +
          ", responseCount=" + responseCount +
          ", active=" + active +
          ", titleFunc='" + titleFunc + '\'' +
          '}';
    }
}
