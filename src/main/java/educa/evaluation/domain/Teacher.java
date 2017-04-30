package educa.evaluation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Teacher extends Person {

    private SchoolGrade grade;

}
