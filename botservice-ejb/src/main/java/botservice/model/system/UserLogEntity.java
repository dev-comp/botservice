package botservice.model.system;

import botservice.model.common.AbstractBaseEntity;
import botservice.util.BotMsgDirectionType;
import botservice.util.BotMsgTransportStatus;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Ceщность, представляющая собой запись лога
 */


@Entity
@Table(name = "userlog")
public class UserLogEntity extends AbstractBaseEntity {

    @ManyToOne
    @JoinColumn(name = "userkey_id")
    private UserKeyEntity userKeyEntity;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date msgTime;

    private String msgBody;

    @NotNull
    @Valid
    @Enumerated(EnumType.STRING)
    private BotMsgDirectionType directionType;

    @NotNull
    @Valid
    @Enumerated(EnumType.STRING)
    private BotMsgTransportStatus transportStatus;

    public UserKeyEntity getUserKeyEntity() {
        return userKeyEntity;
    }

    public void setUserKeyEntity(UserKeyEntity userKeyEntity) {
        this.userKeyEntity = userKeyEntity;
    }

    public Date getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(Date msgTime) {
        this.msgTime = msgTime;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public BotMsgDirectionType getDirectionType() {
        return directionType;
    }

    public void setDirectionType(BotMsgDirectionType directionType) {
        this.directionType = directionType;
    }

    public BotMsgTransportStatus getTransportStatus() {
        return transportStatus;
    }

    public void setTransportStatus(BotMsgTransportStatus transportStatus) {
        this.transportStatus = transportStatus;
    }
}
