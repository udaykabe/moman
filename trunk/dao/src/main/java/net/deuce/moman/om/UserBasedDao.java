package net.deuce.moman.om;

import org.hibernate.Query;

import java.util.List;

@SuppressWarnings("unchecked")
public abstract class UserBasedDao<E extends AbstractEntity> extends EntityDao<E> {

  public UserBasedDao() {
    super();
  }

  public List<E> listByUser(User user) {
    Query query = getSession().createQuery(String.format("select e from %s e where e.user = :user",
        getEntityClass().getName()));
    query.setParameter("user", user);
    return query.list();
  }

  public boolean deleteByUser(User user) {
    Query query = getSession().createQuery(String.format("select e from %s e where e.user = :user",
        getEntityClass().getName(), User.class.getName()));
    query.setParameter("user", user);
    deleteByQuery(query);
    return true;
  }

  private Query buildSelectedQuery(User user) {
    Query query = getSession().createQuery(String.format("select e from %s e where e.user = :user and e.selected = :selected",
        getEntityClass().getName(), User.class.getName()));
    query.setParameter("user", user);
    query.setParameter("selected", Boolean.TRUE);
    return query;
  }

  public List<E> listSelected(User user) {
    return buildSelectedQuery(user).list();
  }

  public E findSelected(User user) {
    return (E) buildSelectedQuery(user).uniqueResult();
  }

}