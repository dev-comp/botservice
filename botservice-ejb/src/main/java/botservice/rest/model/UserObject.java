package botservice.rest.model;

/**
 * Класс для передачи информации об одном ползователе через веб-сервис
 */

public class UserObject {

    private String userName;

    private String botEntryName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBotEntryName() {
        return botEntryName;
    }

    public void setBotEntryName(String botEntryName) {
        this.botEntryName = botEntryName;
    }
}
