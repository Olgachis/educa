package educa.evaluation.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Data
@Entity
public class Parent extends Person {

    @OneToMany(mappedBy = "student")
    private List<Student> children;
}
