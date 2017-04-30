package educa.evaluation.domain;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import javax.persistence.Entity;

@Data
@Entity
public class Program extends  BaseModel{

    @NotBlank
    private String name;

    private String description;


}
