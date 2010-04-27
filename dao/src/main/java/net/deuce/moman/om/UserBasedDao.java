package net.deuce.moman.om;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class UserBasedDao<E extends AbstractEntity> extends EntityDao<E> {

  public UserBasedDao() {
    super();
  }

  protected Criterion getUserRestriction(User user) {
    return Restrictions.eq("user", user);
  }

  @Transactional(readOnly = true)
  public List<E> listByUser(User user) {
    Query query = getEntityManager().createQuery(String.format("select e from %s e, %s u where e.user = u and u.uuid = :uuid",
        getEntityClass().getName(), User.class.getName()));
    query.setParameter("uuid", user.getUuid());
    return query.getResultList();
  }

  @Transactional
  public boolean deleteByUser(User user) {
    Query query = getEntityManager().createQuery(String.format("delete from %s e where e.user = u and u.uuid = :uuid",
      getEntityClass().getName(), User.class.getName()));
    query.setParameter("uuid", user.getUuid());
    deleteByQuery(query);
    return true;
  }

  private Query buildSelectedQuery(User user) {
    Query query = getEntityManager().createQuery(String.format("select e from %s e, %s u where e.user = u and u.uuid = :uuid and e.selected = :selected",
        getEntityClass().getName(), User.class.getName()));
    query.setParameter("uuid", user.getUuid());
    query.setParameter("selected", Boolean.TRUE);
    return query;
  }

  @Transactional(readOnly = true)
  public List<E> listSelected(User user) {
    return buildSelectedQuery(user).getResultList();
  }

  @Transactional(readOnly = true)
  public E findSelected(User user) {
    return (E) buildSelectedQuery(user).getSingleResult();
  }

}