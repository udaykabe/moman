package net.deuce.moman.om;

import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends EntityService<User, UserDao> implements InitializingBean {

  @Autowired
  private UserDao userDao;

  private User staticUser;

  protected UserDao getDao() {
    return userDao;
  }

  @Transactional(readOnly = true)
  public User getDefaultUser() {
    return userDao.get(-1L);
  }

  @Transactional(readOnly = true)
  public User findByUsername(String username) {
    return userDao.findUser(username);
  }

  public void toXml(User user, Document doc) {

    Element root = doc.getRootElement().addElement(getRootElementName());

    for (User u : list()) {
      toXml(u, root);
    }
  }


  public void toXml(User user, Element parent) {
    Element el = parent.addElement("user");
    addElement(el, "id", user.getUuid());
    addElement(el, "username", user.getUsername());
  }

  public Class<User> getType() {
    return User.class;
  }

  public String getRootElementName() {
    return "users";
  }

  public User getStaticUser() {
    return staticUser;
  }

  public void afterPropertiesSet() throws Exception {
    staticUser = findByUsername("nbolton");
  }
}