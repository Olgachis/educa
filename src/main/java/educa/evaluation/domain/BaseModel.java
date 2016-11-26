package educa.evaluation.domain;

import javax.persistence.*;
import java.util.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

@Data
@MappedSuperclass
public abstract class BaseModel {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(updatable = false)
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true, updatable = false)
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date lastUpdated;

    @Version
    private Long version;

    @PrePersist
    public void prePersistBase() {
        dateCreated = new Date();
        lastUpdated = dateCreated;
    }

    @PreUpdate
    public void preUpdateBase() {
        lastUpdated = new Date();
    }

}
