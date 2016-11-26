package educa.evaluation.data;

import lombok.Data;

@Data
public class DbQuestion implements Comparable<DbQuestion> {

    private final String id;
    private final String question;
    private final int priority;
    private final String dependsOn;

    @Override
    public int compareTo(DbQuestion o) {
        return id.compareTo(o.id);
    }
}
