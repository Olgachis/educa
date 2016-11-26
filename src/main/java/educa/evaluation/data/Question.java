package educa.evaluation.data;

import lombok.Data;

@Data
public class Question {
    private final String id;
    private final String type;
    private final String question;
    private final String value;
}
