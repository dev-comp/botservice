package botservice.model.client;

import botservice.model.bot.BotEntity;
import botservice.model.common.AbstractBaseEntity;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Класс, представляющий собой запись о клиентском приложении
 */

@Entity
@Table(name = "clientapp")
public class ClientAppEntity extends AbstractBaseEntity {

    @NotEmpty
    @Size(min = 1, max = 256)
    private String name;

    @OneToOne(optional = false)
    @JoinColumn(name = "bot_id")
    private BotEntity botEntity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private ClientEntity clientEntity;

    @NotEmpty
    @URL
    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BotEntity getBotEntity() {
        return botEntity;
    }

    public void setBotEntity(BotEntity botEntity) {
        this.botEntity = botEntity;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ClientEntity getClientEntity() {
        return clientEntity;
    }

    public void setClientEntity(ClientEntity clientEntity) {
        this.clientEntity = clientEntity;
    }
}
