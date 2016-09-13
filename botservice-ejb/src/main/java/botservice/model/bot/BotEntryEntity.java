package botservice.model.bot;

import botservice.model.common.AbstractBaseEntity;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Класс, представляющий собой запись об экземпляре бота
 */

@Entity
@Table(name = "botentry", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class BotEntryEntity extends AbstractBaseEntity {

    @NotEmpty
    @Size(min = 1, max = 256)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "botadapter_id")
    private BotAdapterEntity botAdapterEntity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BotAdapterEntity getBotAdapterEntity() {
        return botAdapterEntity;
    }

    public void setBotAdapterEntity(BotAdapterEntity botAdapterEntity) {
        this.botAdapterEntity = botAdapterEntity;
    }
}
