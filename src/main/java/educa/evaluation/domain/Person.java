package educa.evaluation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Date;

@Data
public class Person extends BaseModel {

  @NotBlank
  private String name;

  @NotBlank
  private String lastName;

  private String secondLastName;

  private Date birthdate;

  private String email;

  private boolean active;

  //Crees conveniente poner una relacion a muchas instituciones?
  @OneToOne(mappedBy = "institution")
  private Institution institution;

  @ManyToOne
  private PublishedQuestionnaire publishedQuestionnaire;
}
