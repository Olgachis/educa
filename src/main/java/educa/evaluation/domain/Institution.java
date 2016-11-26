package educa.evaluation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Institution extends BaseModel {

    @NotBlank
    private String name;

    @NotBlank
    private String type;

    private Boolean internship;

    private Boolean initialEducation;

    private Boolean preschool;

    private Boolean basic;

    private Boolean secondary;

    private Boolean highSchool;

}
