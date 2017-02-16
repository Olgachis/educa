package educa.evaluation.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionnaireResponses {
    private List<SimpleQuestionnaireResponse> responses;
}
