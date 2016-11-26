package educa.evaluation.data;

import lombok.Data;

import java.util.List;

@Data
public class DbQuestions {
    private final String dependsOn;
    private final List<DbQuestion> questions;
}
