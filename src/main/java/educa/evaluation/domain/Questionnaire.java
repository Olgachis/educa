package educa.evaluation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Questionnaire extends BaseModel {

    @NotBlank
    private String name;

    @OneToMany(mappedBy = "questionnaire")
    private List<Section> sections;

    private Integer period;
}
