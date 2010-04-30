package net.deuce.moman.om;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class UserBasedDao<E extends AbstractEntity> extends EntityDao<E> {

  public UserBasedDao() {
    super();
  }

  public List<E> listByUser(User user) {
    Query query = getEntityManager().createQuery(String.format("select e from %s e, %s u where e.user = u and u.id = :id",
        getEntityClass().getName(), User.class.getName()));
    query.setParameter("id", user.getId());
    return query.getResultList();
  }

  public boolean deleteByUser(User user) {
    Query query = getEntityManager().createQuery(String.format("delete from %s e where e.user = u and u.id = :id",
      getEntityClass().getName(), User.class.getName()));
    query.setParameter("id", user.getId());
    deleteByQuery(query);
    return true;
  }

  private Query buildSelectedQuery(User user) {
    Query query = getEntityManager().createQuery(String.format("select e from %s e, %s u where e.user = u and u.id = :id and e.selected = :selected",
        getEntityClass().getName(), User.class.getName()));
    query.setParameter("id", user.getId());
    query.setParameter("selected", Boolean.TRUE);
    return query;
  }

  public List<E> listSelected(User user) {
    return buildSelectedQuery(user).getResultList();
  }

  public E findSelected(User user) {
    return (E) buildSelectedQuery(user).getSingleResult();
  }

}