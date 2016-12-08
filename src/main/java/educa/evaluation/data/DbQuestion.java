package educa.evaluation.data;

import lombok.Data;

import java.util.List;

@Data
public class DbQuestion implements Comparable<DbQuestion> {

    private final String id;
    private final String question;
    private final int priority;
    private final String dependsOn;
    private final List<OptionData> options;

    @Override
    public int compareTo(DbQuestion o) {
        return id.compareTo(o.id);
    }
}
