package botservice.model.bot;

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

    public Map<String, String> getProps() {
        return props;
    }

    public void setProps(Map<String, String> props) {
        this.props = props;
    }
}
