package net.deuce.moman.om;


import net.deuce.moman.util.Utils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AllocationSetService extends UserBasedService<AllocationSet, AllocationSetDao> {

  @Autowired
  private AllocationService allocationService;

  @Autowired
  private AllocationSetDao allocationSetDao;

  protected AllocationSetDao getDao() {
    return allocationSetDao;
  }

  public boolean doesNameExist(User user, String name) {
    for (AllocationSet allocationSet : getEntities(user)) {
      if (allocationSet.getName().equals(name)) return true;
    }
    return false;
  }

  public void moveAllocations(AllocationSet allocationSet, List<Integer> indexes, Allocation target, boolean before) {
    int startIndex = target.getIndex();
    if (before) {
      startIndex--;
    }

    Allocation allocation;
    List<Allocation> allocations = allocationSet.getAllocations();

    for (int i = startIndex; i < startIndex + indexes.size(); i++) {
      allocation = allocations.get(indexes.get(i - startIndex));
      allocation.setIndex(i);
      allocationService.update(allocation);
    }

    for (int i = startIndex + indexes.size(); i < allocations.size(); i++) {
      allocation = allocations.get(i);
      allocation.setIndex(allocations.get(i).getIndex() + indexes.size());
      allocationService.update(allocation);
    }
    Collections.sort(allocations);
    update(allocationSet);
  }

  public Class<AllocationSet> getType() {
    return AllocationSet.class;
  }

  public void toXml(AllocationSet allocationSet, Element root) {
    Element el;
    Element sel;

    el = root.addElement("allocation-set");
    el.addAttribute("id", allocationSet.getUuid());
    addElement(el, "name", allocationSet.getName());
    if (allocationSet.getIncome() != null) {
      el.addElement("income").addAttribute("id", allocationSet.getIncome().getUuid());
    }

    sel = el.addElement(allocationService.getRootElementName());
    for (Allocation allocation : allocationSet.getAllocations()) {
      allocationService.toXml(allocation, sel);
    }
  }

  public void toXml(User user, Document doc) {

    Element root = doc.getRootElement().addElement(getRootElementName());

    for (AllocationSet allocationSet : getEntities(user)) {
      toXml(allocationSet, root);
    }
  }

  public String getRootElementName() {
    return "allocation-sets";
  }
}