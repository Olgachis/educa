package educa.evaluation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;
import javax.persistence.OneToOne;

import javax.persistence.Entity;

@Data
@Entity
public class Student extends Person {

    @OneToOne(mappedBy = "schoolGrade")
    private SchoolGrade schoolGrade;

    //Para relacionar un estudiante con su papa
    @OneToOne(mappedBy = "person")
    private Person parent;

}
