package botservice.model.bot;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;

/**
 * Класс, представляющий адаптер бота
 */

@Entity
@Table(name = "botadapter", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class BotAdapterEntity extends BotBaseEntity{

    @NotEmpty
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
