package botservice.model.system;

import botservice.model.bot.BotEntryEntity;
import botservice.model.common.AbstractBaseEntity;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Map;

/**
 * Сущность, представляющая информацию о пользователе
 */

@Entity
@Table(name = "userkey", uniqueConstraints = @UniqueConstraint(columnNames = {"username", "botentry_id"}))
public class UserKeyEntity extends AbstractBaseEntity {

    @NotEmpty
    private String userName;

    @ManyToOne
    @JoinColumn(name = "botentry_id")
    private BotEntryEntity botEntryEntity;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "userkeyprop", joinColumns = {@JoinColumn(name = "userkey_id")})
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> props;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BotEntryEntity getBotEntryEntity() {
        return botEntryEntity;
    }

    public void setBotEntryEntity(BotEntryEntity botEntryEntity) {
        this.botEntryEntity = botEntryEntity;
    }

    public Map<String, String> getProps() {
        return props;
    }

    public void setProps(Map<String, String> props) {
        this.props = props;
    }
}
