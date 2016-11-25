package educa.evaluation.domain;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "questionnaire_section")
public class Section extends BaseModel {

    @ManyToOne
    private Questionnaire questionnaire;

    @Lob
    @NotBlank
    @Type(type = "org.hibernate.type.TextType")
    private String questionJson;

}
