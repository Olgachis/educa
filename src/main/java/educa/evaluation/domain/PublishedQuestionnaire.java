package educa.evaluation.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "published_questionnaire")
@EqualsAndHashCode(callSuper = true)
public class PublishedQuestionnaire extends BaseModel{

    @NotBlank
    @ManyToMany(mappedBy = "simpleQuestionnaire")
    private SimpleQuestionnaire simpleQuestionnaire;

    @NotBlank
    @OneToOne(mappedBy = "institution")
    private Institution institution;

    private String schoolPeriod;

    private Boolean active;

    private Date expirationDate;

    private QuestionnaireStatus status;

}
