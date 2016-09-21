package botservice.model.system;

import botservice.model.bot.BotEntity;
import botservice.model.common.AbstractBaseEntity;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Map;

/**
 * Сущность, представляющая информацию о пользователе
 */

@Entity
@Table(name = "userkey", uniqueConstraints = @UniqueConstraint(columnNames = {"username", "bot_id"}))
public class UserKeyEntity extends AbstractBaseEntity {

    @NotEmpty
    private String userName;

    @ManyToOne
    @JoinColumn(name = "bot_id")
    private BotEntity botEntity;

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

    public BotEntity getBotEntity() {
        return botEntity;
    }

    public void setBotEntity(BotEntity botEntity) {
        this.botEntity = botEntity;
    }

    public Map<String, String> getProps() {
        return props;
    }

    public void setProps(Map<String, String> props) {
        this.props = props;
    }
}
