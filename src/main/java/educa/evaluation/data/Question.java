package educa.evaluation.data;

import lombok.Data;

import java.util.List;

@Data
public class Question {
    private final String id;
    private final String type;
    private final String question;
    private final String value;
    private final boolean valuable;
    private final List<OptionData> options;
    private final int priority;

    @java.lang.Override
    public java.lang.String toString() {
        return "Question{" +
          "id='" + id + '\'' +
          ", type='" + type + '\'' +
          ", question='" + question + '\'' +
          ", value='" + value + '\'' +
          ", valuable=" + valuable +
          ", options=" + options +
          ", priority=" + priority +
          '}';
    }
}

