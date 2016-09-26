package botservice.web.controller.bot.common;

/**
 * todo@shapoval add class description
 * <p>
 * date: 26.09.2016
 *
 * @author p.shapoval
 */
public class Link {
  private String viewId;
  private String outcome;
  private String id;
  private String value;
  private String styleClass;

  public Link(String outcome, String id, String value, String styleClass) {
    this.viewId = outcome;
    this.outcome = outcome;
    this.id = id;
    this.value = value;
    this.styleClass = styleClass;
  }

  public String getViewId() {
    return viewId;
  }

  public void setViewId(String viewId) {
    this.viewId = viewId;
  }

  public String getOutcome() {
    return outcome;
  }

  public void setOutcome(String outcome) {
    this.outcome = outcome;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getStyleClass() {
    return styleClass;
  }

  public void setStyleClass(String styleClass) {
    this.styleClass = styleClass;
  }
}
