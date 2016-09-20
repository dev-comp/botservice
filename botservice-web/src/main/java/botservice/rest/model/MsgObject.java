package botservice.rest.model;

/**
 * Класс, представляющий собой сообщение
 */
public class MsgObject {

    private UserObject userObject;

    private String msgBody;

    public UserObject getUserObject() {
        return userObject;
    }

    public void setUserObject(UserObject userObject) {
        this.userObject = userObject;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }
}
