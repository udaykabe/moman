package net.deuce.moman.om;

import net.deuce.moman.util.Utils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AllocationService extends UserBasedService<Allocation, AllocationDao> {

  @Autowired
  private AllocationDao allocationDao;

  protected AllocationDao getDao() {
    return allocationDao;
  }

  public void toXml(User user, Document doc) {

    Element root = doc.getRootElement().addElement(getRootElementName());

    for (Allocation allocation: getEntities(user)) {
      toXml(allocation, root);
    }
  }

  public void toXml(Allocation allocation, Element parent) {
    Element el = parent.addElement("allocation");
    el.addAttribute("id", allocation.getUuid());
    addElement(el, "index", allocation.getIndex());
    addElement(el, "amount", Utils.formatDouble(allocation.getAmount()));
    addElement(el, "amount-type", allocation.getAmountType().name());
    addElement(el, "limit", Utils.formatDouble(allocation.getLimit()));
    addElement(el, "limit-type", allocation.getLimitType().name());
    addElement(el, "enabled", allocation.getEnabled());
    el.addElement("envelope").addAttribute("id", allocation.getEnvelope().getUuid());
  }

  public String getRootElementName() {
    return "allocations";
  }

  public Class<Allocation> getType() {
    return Allocation.class;
  }
}