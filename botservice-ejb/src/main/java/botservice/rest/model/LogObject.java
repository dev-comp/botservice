package botservice.rest.model;

import java.util.Date;

/**
 * Класс, представляющий собой запись лога
 */
public class LogObject {

    private MsgObject msgObject;

    private Date msgTime;

    public MsgObject getMsgObject() {
        return msgObject;
    }

    public void setMsgObject(MsgObject msgObject) {
        this.msgObject = msgObject;
    }

    public Date getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(Date msgTime) {
        this.msgTime = msgTime;
    }
}
