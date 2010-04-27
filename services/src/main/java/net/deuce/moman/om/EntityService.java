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
      persist(entity);
      return entity;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Delete checks the delete permission and sets the delete flag.
   */
  public boolean delete(E entity) {
    return getDao().delete(entity);
  }

  /**
   * Retrieve an entity based on the generated id.
   */
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
  public List<E> list() {
    return getDao().list();
  }

  /**
   * Attempts to save, using the create permission in the security framework.
   */
  public E persist(E entity) {
    getDao().persist(entity);
    return entity;
  }

  public void setEntityClass(Class<E> entityClass) {
    this.entityClass = entityClass;
  }

  /**
   * Checks the update permission and persists the entity.
   *
   * @param entity
   * @return
   */
  public E update(E entity) {
    getDao().persist(entity);
    return entity;
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
