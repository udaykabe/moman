package net.deuce.moman.om;


import net.deuce.moman.job.AbstractCommand;
import net.deuce.moman.job.Command;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

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

  public Command moveAllocationsCommand(final AllocationSet allocationSet, final List<Integer> indexes, final Allocation target, final boolean before) {
    return new AbstractCommand(AllocationSet.class.getSimpleName() + " moveAllocations(" + allocationSet.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        final SortedSet<Allocation> oldList = allocationSet.getAllocations();

        moveAllocations(allocationSet, indexes, target, before);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {

            int i=0;
            for (Allocation a : oldList) {
              a.setIndex(i++);
            }
          }
        });
      }
    };
  }

  @Transactional
  public void moveAllocations(AllocationSet allocationSet, List<Integer> indexes, Allocation target, boolean before) {
    int startIndex = target.getIndex();
    if (before) {
      startIndex--;
    }

    Allocation allocation;
    List<Allocation> allocations = new ArrayList<Allocation>(allocationSet.getAllocations());

    for (int i = startIndex; i < startIndex + indexes.size(); i++) {
      allocation = allocations.get(indexes.get(i - startIndex));
      allocation.setIndex(i);
      allocationService.saveOrUpdate(allocation);
    }

    for (int i = startIndex + indexes.size(); i < allocations.size(); i++) {
      allocation = allocations.get(i);
      allocation.setIndex(allocations.get(i).getIndex() + indexes.size());
      allocationService.saveOrUpdate(allocation);
    }
    Collections.sort(allocations);
    saveOrUpdate(allocationSet);
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

  public String getRootElementName() {
    return "allocation-sets";
  }
}