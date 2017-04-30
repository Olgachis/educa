package educa.evaluation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Parent extends Person {

    @OneToMany(mappedBy = "student")
    private List<Student> children;
}
