package educa.evaluation.domain;


import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "published_questionnaire")
public class PublishedQuestionnaire extends BaseModel{

    //Es necesario poner la relacion? o al poner solo la referencia al objeto
    @NotBlank
    @OneToOne(mappedBy = "simpleQuestionnaire")
    SimpleQuestionnaire simpleQuestionnaire;

    @NotBlank
    @OneToOne(mappedBy = "institution")
    Institution institution;

    //2017-2018
    String schoolPeriod;

    Boolean active;

    Date expirationDate;

    QuestionnaireStatus status;

}
