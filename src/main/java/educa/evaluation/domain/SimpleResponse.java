package educa.evaluation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class SimpleResponse extends BaseModel {

    @NotNull
    private String title;

    @ManyToOne
    private SimpleQuestionnaire questionnaire;

    @NotNull
    @ManyToOne
    private PublishedQuestionnaire publishedQuestionnaire;

    @Lob
    @NotBlank
    @Type(type = "org.hibernate.type.TextType")
    private String simpleResponse;

}
