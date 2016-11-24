package educa.evaluation.domain;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;

@Data
@Entity
public class Permission extends BaseModel {

   @NotBlank
   private String name;

}
