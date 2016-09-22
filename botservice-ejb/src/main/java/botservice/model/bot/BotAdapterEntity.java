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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "botadapteranswer", joinColumns = {@JoinColumn(name = "botadapter_id")})
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> answers;

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

    public Map<String, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }
}
