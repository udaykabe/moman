package net.deuce.moman.om;

import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class UserBasedService<E extends AbstractEntity, ED extends UserBasedDao<E>> extends EntityService<E, ED> {


  public boolean isClearable() {
    return true;
  }

  public List<E> getEntities(User user) {
    return list();
  }

  @Transactional(readOnly = true)
  public List<E> listByUser(User user) {
    return getDao().listByUser(user);
  }

  public List<E> getOrderedEntities(User user, boolean reverse) {
    List<E> list = listByUser(user);
    if (list.size() > 0) {
      E entity = list.get(0);
      Comparator<E> comparator = reverse ? entity.getReverseComparator() :
          entity.getForwardComparator();
      Collections.sort(list, comparator);
    }
    return list;
  }

  @Transactional
  public void setEntities(User user, List<E> entities) {
    clearEntities(user);
    if (entities != null) {
      for (E entity : entities) {
        doAddEntity(entity);
      }
    }
  }

  @Transactional
  public void clearEntities(User user) {
    getDao().deleteByUser(user);
  }

}
