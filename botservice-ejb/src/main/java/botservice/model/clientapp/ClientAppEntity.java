package botservice.model.clientapp;

import botservice.model.bot.BotEntryEntity;
import botservice.model.common.AbstractBaseEntity;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Класс, представляющий собой запись о клиентском приложении
 */

@Entity
@Table(name = "clientapp", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class ClientAppEntity extends AbstractBaseEntity {

    @NotEmpty
    @Size(min = 1, max = 256)
    private String name;

    @OneToOne(optional = false)
    @JoinColumn(name = "botentry_id")
    private BotEntryEntity botEntryEntity;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BotEntryEntity getBotEntryEntity() {
        return botEntryEntity;
    }

    public void setBotEntryEntity(BotEntryEntity botEntryEntity) {
        this.botEntryEntity = botEntryEntity;
    }
}
