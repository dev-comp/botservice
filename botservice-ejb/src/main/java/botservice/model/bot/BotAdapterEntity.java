package botservice.model.bot;

import botservice.model.common.AbstractBaseEntity;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Класс, представляющий адаптер бота
 */

@Entity
@Table(name = "botadapter", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class BotAdapterEntity extends AbstractBaseEntity{

    @NotEmpty
    @Size(min = 1, max = 256)
    private String name;

    private String filePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
