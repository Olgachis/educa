package educa.evaluation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "educa_user")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseModel {

    @NotBlank
    private String username;

    @JsonIgnore
    private String password;

    @NotNull
    @ManyToOne
    private Role role;

    @ManyToOne
    private Institution institution;

}
