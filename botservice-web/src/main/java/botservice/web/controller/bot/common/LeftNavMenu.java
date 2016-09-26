package botservice.web.controller.bot.common;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * todo@shapoval add class description
 * <p>
 * date: 26.09.2016
 *
 * @author p.shapoval
 */
@Named
@ViewScoped
public class LeftNavMenu implements Serializable {

  private String header;
  private List<Link> links = new ArrayList<>();

//  private static final String LINK_CSS_STYLE_BTN_BTN_DEFAULT_NAV = "btn btn-default nav";
  private static final String LINK_CSS_STYLE_BTN_BTN_DEFAULT_NAV = "btn btn-primary btn-block ";
//  private static final String LINK_CSS_STYLE_BTN_BTN_DEFAULT_NAV = "";

  {
    links.add(new Link("/index.xhtml", "linkToMain", "Home", LINK_CSS_STYLE_BTN_BTN_DEFAULT_NAV));
    links.add(new Link("/content/bot/botAdapter/botAdapterList.xhtml", "linkToBotAdapters", "Bot adapters", LINK_CSS_STYLE_BTN_BTN_DEFAULT_NAV));
    links.add(new Link("/content/bot/bot/botList.xhtml", "linkToBots", "Bots", LINK_CSS_STYLE_BTN_BTN_DEFAULT_NAV));
    links.add(new Link("/content/client/client/clientList.xhtml", "linkToClients", "Clients", LINK_CSS_STYLE_BTN_BTN_DEFAULT_NAV));
    links.add(new Link("/content/client/clientapp/clientAppList.xhtml", "linkToClientApps", "Client applications", LINK_CSS_STYLE_BTN_BTN_DEFAULT_NAV));
    links.add(new Link("/content/system/userKeyList.xhtml", "linkToUserKeys", "User keys", LINK_CSS_STYLE_BTN_BTN_DEFAULT_NAV));
    links.add(new Link("/content/system/userLogList.xhtml", "linkToUserLog", "User log", LINK_CSS_STYLE_BTN_BTN_DEFAULT_NAV));
  }

  public void setHeader(String header) {
    this.header = header;
  }

  public String getHeader() {
    return header;
  }

  public void setLinks(List<Link> links) {
    this.links = links;
  }

  public List<Link> getLinks() {
    return links;
  }
}
