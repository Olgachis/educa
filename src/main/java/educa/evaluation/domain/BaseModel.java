package educa.evaluation.domain;

import javax.persistence.*;
import java.util.*;

import org.hibernate.annotations.GenericGenerator;

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

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the dateCreated
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * @return the lastUpdated
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * @param lastUpdated the lastUpdated to set
     */
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

}
