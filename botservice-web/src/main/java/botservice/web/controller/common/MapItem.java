package botservice.web.controller.common;

/**
 * Класс для хранения пары ключ-значение
 */
public class MapItem {
    String key;
    String value;

    public MapItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}
