package net.deuce.moman.om;

import net.deuce.moman.util.Utils;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertService extends UserBasedService<Alert, AlertDao> {

  @Autowired
  private AlertDao alertDao;

  protected AlertDao getDao() {
    return alertDao;
  }

  public void toXml(Alert alert, Element parent) {
    Element el = parent.addElement("alert");
    el.addAttribute("id", alert.getUuid());
    addElement(el, "alertType", alert.getAlertType().name());
    if (alert.getTransaction() != null) {
      el.addElement("transaction").addAttribute("id", alert.getTransaction().getUuid());
    }
    if (alert.getEnvelope() != null) {
      el.addElement("envelope").addAttribute("id", alert.getEnvelope().getUuid());
    }
  }

  public Class<Alert> getType() {
    return Alert.class;
  }

  public String getRootElementName() {
    return "alerts";
  }

}