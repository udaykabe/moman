package net.deuce.moman.om;

import net.deuce.moman.util.Utils;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SplitService extends UserBasedService<Split, SplitDao> {

  @Autowired
  private SplitDao splitDao;

  protected SplitDao getDao() {
    return splitDao;
  }

  public void toXml(Split split, Element parent) {
    Element el = parent.addElement("envelope");
    el.addAttribute("id", split.getUuid());
    addElement(el, "amount", Utils.formatDouble(split.getAmount()));
    el.addElement("envelope").addAttribute("id", split.getEnvelope().getUuid());
    el.addElement("transaction").addAttribute("id", split.getTransaction().getUuid());
  }

  public Class<Split> getType() {
    return Split.class;
  }

  public String getRootElementName() {
    return "split";
  }

}