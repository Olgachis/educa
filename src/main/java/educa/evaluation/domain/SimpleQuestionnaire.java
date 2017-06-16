package educa.evaluation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class SimpleQuestionnaire extends BaseModel {

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String titleFunc;

    @NotBlank
    private String title;

    private Boolean active;

    @Lob
    @NotBlank
    @Type(type = "org.hibernate.type.TextType")
    private String questionsJson;

}
