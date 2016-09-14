package botservice.model.bot;

import botservice.model.common.AbstractBaseEntity;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 */

@MappedSuperclass
public abstract class BotBaseEntity extends AbstractBaseEntity {

    @NotEmpty
    @Size(min = 1, max = 256)
    private String name;

    @NotNull
    @Range(min = 0, max = 1)
    private int state;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
