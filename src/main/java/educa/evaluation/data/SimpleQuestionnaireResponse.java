package educa.evaluation.data;

import lombok.Data;

import java.util.Map;

@Data
public class SimpleQuestionnaireResponse {
    private String id;
    private String title;
    private Map<String, Object> data;
}
