package educa.evaluation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import javax.persistence.Entity;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Student extends Person {

    @OneToOne(mappedBy = "schoolGrade")
    private SchoolGrade schoolGrade;

    //Para relacionar un estudiante con su papa
    @OneToMany(mappedBy = "person")
    private Person parent;

}
