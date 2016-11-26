package educa.evaluation.data;

import lombok.Data;

import java.util.List;

@Data
public class SubdimensionResponse {

    private final List<QuestionResponse> responses;

    private final String response;

}
