package botservice.model.bot;

import javax.persistence.*;

/**
 * Класс, представляющий собой запись об экземпляре бота
 */

@Entity
@Table(name = "botentry", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class BotEntryEntity extends BotBaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "botadapter_id")
    private BotAdapterEntity botAdapterEntity;

    public BotAdapterEntity getBotAdapterEntity() {
        return botAdapterEntity;
    }

    public void setBotAdapterEntity(BotAdapterEntity botAdapterEntity) {
        this.botAdapterEntity = botAdapterEntity;
    }
}
