package net.deuce.moman.om;

import net.deuce.moman.util.Utils;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AllocationService extends UserBasedService<Allocation, AllocationDao> {

  @Autowired
  private AllocationDao allocationDao;

  protected AllocationDao getDao() {
    return allocationDao;
  }

  public void toXml(Allocation allocation, Element parent) {
    Element el = parent.addElement("allocation");
    el.addAttribute("id", allocation.getUuid());
    addElement(el, "index", allocation.getIndex());
    addElement(el, "amount", Utils.formatDouble(allocation.getAmount()));
    addElement(el, "amountType", allocation.getAmountType().name());
    addElement(el, "limit", Utils.formatDouble(allocation.getLimit()));
    addElement(el, "limitType", allocation.getLimitType().name());
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