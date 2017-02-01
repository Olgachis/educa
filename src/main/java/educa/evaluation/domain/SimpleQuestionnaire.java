package educa.evaluation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class SimpleQuestionnaire extends BaseModel {

    @Lob
    @NotBlank
    private String titleFunc;

    @NotBlank
    private String title;

    @Lob
    @NotBlank
    private String questionsJson;
}
