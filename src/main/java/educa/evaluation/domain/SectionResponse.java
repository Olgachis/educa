package educa.evaluation.domain;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class SectionResponse extends BaseModel {

    @NotNull
    @ManyToOne
    private Section section;

    @NotNull
    @ManyToOne
    private User user;

    @Lob
    @NotBlank
    @Type(type = "org.hibernate.type.TextType")
    private String responseJson;

    private String comments;

}
