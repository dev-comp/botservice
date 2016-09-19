package botservice.model.client;

import botservice.model.common.AbstractBaseEntity;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

/**
 * Сервис, представляющий собой запись о клиенте (организации)
 */

@Entity
@Table(name = "client", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class ClientEntity extends AbstractBaseEntity {

    @NotEmpty
    @Size(min = 1, max = 256)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
