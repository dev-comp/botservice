package botservice.model.bot;

import botservice.model.client.ClientAppEntity;

import javax.persistence.*;
import java.util.Map;

/**
 * Класс, представляющий собой запись об экземпляре бота
 */

@Entity
@Table(name = "botentry", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class BotEntryEntity extends BotBaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "botadapter_id")
    private BotAdapterEntity botAdapterEntity;

    @OneToOne(mappedBy = "botEntryEntity")
    private ClientAppEntity clientAppEntity;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "botentryprop", joinColumns = {@JoinColumn(name = "botentry_id")})
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> props;

    public BotAdapterEntity getBotAdapterEntity() {
        return botAdapterEntity;
    }

    public void setBotAdapterEntity(BotAdapterEntity botAdapterEntity) {
        this.botAdapterEntity = botAdapterEntity;
    }

    public ClientAppEntity getClientAppEntity() {
        return clientAppEntity;
    }

    public void setClientAppEntity(ClientAppEntity clientAppEntity) {
        this.clientAppEntity = clientAppEntity;
    }

    public Map<String, String> getProps() {
        return props;
    }

    public void setProps(Map<String, String> props) {
        this.props = props;
    }
}
