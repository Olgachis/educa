package educa.evaluation.data;

import lombok.Data;

import java.util.List;

@Data
public class Question {
    private final String id;
    private final String type;
    private final String question;
    private final String value;
    private final List<OptionData> options;
}
