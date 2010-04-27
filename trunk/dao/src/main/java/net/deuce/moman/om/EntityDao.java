package net.deuce.moman.om;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class EntityDao<E extends AbstractEntity> {

  protected Class<E> entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  private final Logger log = LoggerFactory.getLogger(getClass());

  @PersistenceContext(unitName = "moman")
  protected EntityManager em;

  public EntityDao() {
    this.entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }

  public EntityManager getEntityManager() {
    return em;
  }

  @Transactional
  public boolean delete(E entity) {
    em.remove(entity);
    return true;
  }

  @Transactional
  public boolean deleteByQuery(Query query) {

    Iterator<E> itr = query.getResultList().iterator();

    int count = 0;
    while (itr.hasNext()) {
      delete(itr.next());
      if (++count % 100 == 0) {
        //flush a batch of updates and release memory:
        em.flush();
        em.clear();
      }
    }

    return true;
  }

  /**
   * Retrieve an entity based on the generated id.
   */
  @Transactional(readOnly = true)
  public E get(Long id) {
    E entity = (E) em.find(entityClass, id);
    return entity;
  }

  /**
   * Retrieve an entity based on the uuid.
   */
  @Transactional(readOnly = true)
  public E get(String uuid) {
    Query query = em.createQuery(String.format("select e from %s e where e.UUID = :uuid", getEntityClass().getName()));
    query.setParameter("uuid", uuid);
    E entity = (E) query.getSingleResult();
    return entity;
  }

  public Class<E> getEntityClass() {
    return entityClass;
  }

  @Transactional(readOnly = true)
  public List<E> list() {
    return em.createQuery(String.format("select e from %s e", getEntityClass().getName())).getResultList();
  }

  @Transactional
  public E persist(E entity) {
    em.persist(entity);
    return entity;
  }

  public void setEntityClass(Class<E> entityClass) {
    this.entityClass = entityClass;
  }

  protected static class Parameter {
    private String name;
    private Object value;

    public Parameter(String name, Object value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public Object getValue() {
      return value;
    }
  }

}
