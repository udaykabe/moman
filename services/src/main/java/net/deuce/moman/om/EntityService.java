package net.deuce.moman.om;

import net.deuce.moman.util.Utils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class EntityService<E extends AbstractEntity, ED extends EntityDao<E>> {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private Class<E> entityClass;

  /**
   * This constructor uses some reflection magic to infer the type of entity that this service manages.  It needs to be passed explicitly in the future.
   */
  @SuppressWarnings("unchecked")
  public EntityService() {
    super();
    entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }

  protected abstract ED getDao();

  public String createUuid() {
    return Utils.createUuid();
  }

  /**
   * Delete checks the delete permission and sets the delete flag.
   */
  @Transactional
  public boolean deleteByUuid(String uuid) {
    E e = getByUuid(uuid);
    if (e != null) {
      return delete(e);
    }
    return false;
  }

  public E newEntity() {
    try {
      E entity = entityClass.newInstance();
      entity.setUuid(createUuid());
      return entity;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Delete checks the delete permission and sets the delete flag.
   */
  @Transactional
  public boolean delete(E entity) {
    return getDao().delete(entity);
  }

  /**
   * Retrieve an entity based on the generated id.
   */
  @Transactional(readOnly = true)
  public E get(Long id) {
    return getDao().get(id);
  }

  /**
   * Retrieve an entity based on the uuid.
   */
  public E getByUuid(String uuid) {
    return get(uuid);
  }

  /**
   * Retrieve an entity based on the uuid.
   */
  @Transactional(readOnly = true)
  public E get(String uuid) {
    return getDao().get(uuid);
  }

  /**
   * Returns the type that this service manages.
   *
   * @return its declared class
   */
  public Class<E> getEntityClass() {
    return entityClass;
  }

  /**
   * Simple list operation.
   */
  @Transactional(readOnly = true)
  public List<E> list() {
    return getDao().list();
  }

  @Transactional(rollbackFor = Exception.class)
  public E saveOrUpdate(E entity) {
    return getDao().saveOrUpdate(entity);
  }

  /*
  @Transactional(rollbackFor = Exception.class)
  public E persist(E entity) {
    return getDao().persist(entity);
  }

  @Transactional
  public E update(E entity) {
    return getDao().update(entity);
  }
  */

  public boolean entityExists(E entity) {
    return get(entity.getUuid()) != null;
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

  @Transactional(readOnly = true)
  public void addEntity(E entity) {
    doAddEntity(entity);
  }

  protected void doAddEntity(E entity) {
    if (entity.getUuid() == null) {
      throw new RuntimeException("No uuid set");
    }

    if (entity.getUuid() != null && entityExists(entity)) {
      throw new RuntimeException("Duplicate entity uuid: " + entity.getUuid());
    }

    getDao().saveOrUpdate(entity);
  }

  public void removeEntity(E entity) {
    delete(entity);
  }

  public Document buildXml(List<E> entities) {
    Document doc = DocumentHelper.createDocument();

    Element root = doc.addElement(getRootElementName());
    for (E entity : entities) {
      toXml(entity, root);
    }

    return doc;
  }

  protected void addElement(Element el, String elementName, Object value) {
    el.addElement(elementName).setText(value != null ? value.toString() : "");
  }

  protected void addOptionalElement(Element el, String elementName, Object obj) {
    if (obj != null) {
      el.addElement(elementName).setText(obj.toString());
    }
  }

  protected void addOptionalBooleanElement(Element el, String elementName, Boolean booleanValue) {
    if (booleanValue != null && booleanValue.booleanValue()) {
      el.addElement(elementName).setText(booleanValue.toString());
    }
  }

  public abstract Class<E> getType();

  public abstract String getRootElementName();

  public abstract void toXml(E entity, Element root);

  public abstract void toXml(User user, Document root);
}
