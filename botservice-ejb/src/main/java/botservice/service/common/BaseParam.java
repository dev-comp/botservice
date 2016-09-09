package botservice.service.common;

import javax.persistence.metamodel.SingularAttribute;

/**
 * Параметр
 */
public class BaseParam {

    private SingularAttribute attribute;

    private Object value;

    public BaseParam(SingularAttribute attribute, Object value){
        this.attribute = attribute;
        this.value = value;
    }

    public SingularAttribute getAttribute() {
        return attribute;
    }

    public Object getValue() {
        return value;
    }
}
