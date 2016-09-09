package botservice.model.common;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Класс-предок для сохранемых объектов
 */

@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class AbstractBaseEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        AbstractBaseEntity abstractBaseEntity = (AbstractBaseEntity)obj;
        return abstractBaseEntity.getId() != null && getId() != null && abstractBaseEntity.getId().equals(getId());
    }

}
