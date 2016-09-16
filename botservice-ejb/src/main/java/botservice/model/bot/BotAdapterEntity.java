package botservice.model.bot;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Map;

/**
 * Класс, представляющий адаптер бота
 */

@Entity
@Table(name = "botadapter", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class BotAdapterEntity extends BotBaseEntity{

    @NotEmpty
    private String filePath;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "botadapterprop", joinColumns = {@JoinColumn(name = "botadapter_id")})
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> props;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Map<String, String> getProps() {
        return props;
    }

    @SuppressWarnings("unused")
    public void setProps(Map<String, String> props) {
        this.props = props;
    }
}
