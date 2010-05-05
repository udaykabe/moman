package net.deuce.moman.om;

import net.deuce.moman.util.Utils;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayeeService extends UserBasedService<Payee, PayeeDao> {

  @Autowired
  private PayeeDao payeeDao;

  protected PayeeDao getDao() {
    return payeeDao;
  }

  public void toXml(Payee payee, Element parent) {
    Element el = parent.addElement("payee");
    el.addAttribute("id", payee.getUuid());
    addElement(el, "description", payee.getDescription());
    addElement(el, "amount", Utils.formatDouble(payee.getAmount()));
    el.addElement("envelope").addAttribute("id", payee.getEnvelope().getUuid());
  }

  public Class<Payee> getType() {
    return Payee.class;
  }

  public String getRootElementName() {
    return "payees";
  }

}