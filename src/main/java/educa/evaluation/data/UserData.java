package educa.evaluation.data;

import lombok.Data;

@Data
public class UserData {

    private final String institution;
    private final String username;
    private final String role;
    private final boolean openQuestionnaire;

}
