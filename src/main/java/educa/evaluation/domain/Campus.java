package educa.evaluation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Campus extends BaseModel {

    @ManyToOne
    private Institution institution;

    @NotBlank
    private String name;

    @NotBlank
    private String campusName;

    @NotBlank
    private String type;

    private Boolean openQuestionnaire;

    private Boolean internship;

    private Boolean initialEducation;

    private Boolean preschool;

    private Boolean basic;

    private Boolean secondary;

    private Boolean highSchool;

    private Boolean primaryCampus;

    public String getName() {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        if(campusName != null) {
            builder.append(" / ");
            builder.append(campusName);
        }
        return builder.toString();
    }

}
