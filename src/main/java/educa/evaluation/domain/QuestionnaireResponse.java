package educa.evaluation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class QuestionnaireResponse extends BaseModel {

    @ManyToOne
    private Campus campus;

    @Lob
    @NotBlank
    @Type(type = "org.hibernate.type.TextType")
    private String responseJson;

}
