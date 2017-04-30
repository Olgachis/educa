package educa.evaluation.domain;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class Teacher extends Person {

    private SchoolGrade grade;

}
