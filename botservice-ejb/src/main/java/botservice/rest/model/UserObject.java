package botservice.rest.model;

/**
 * Класс для передачи информации об одном ползователе через веб-сервис
 */

public class UserObject {

    private String userName;

    private String botName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }
}
