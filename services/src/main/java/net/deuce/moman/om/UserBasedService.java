package net.deuce.moman.om;

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

  public boolean entityExists(String uuid) {
    return findEntity(uuid) != null;
  }

  public E findEntity(String uuid) {
    return getByUuid(uuid);
  }

  public E getEntity(String uuid) {
    E entity = findEntity(uuid);
    if (entity == null) {
      throw new RuntimeException("No entity exists with UUID " + uuid);
    }
    return entity;
  }

  public void addEntity(E entity) {
    if (entity.getUuid() == null) {
      throw new RuntimeException("No uuid set");
    }

    if (entity.getUuid() != null && entityExists(entity.getUuid())) {
      throw new RuntimeException("Duplicate entity uuid: " + entity.getUuid());
    }

    persist(entity);
  }

  public void removeEntity(E entity) {
    delete(entity);
  }

  public void setEntities(User user, List<E> entities) {
    clearEntities(user);
    if (entities != null) {
      for (E entity : entities) {
        addEntity(entity);
      }
    }
  }

  public void clearEntities(User user) {
    getDao().deleteByUser(user);
  }

}
