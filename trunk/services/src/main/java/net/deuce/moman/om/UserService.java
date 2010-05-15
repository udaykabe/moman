package net.deuce.moman.om;

import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends EntityService<User, UserDao> implements InitializingBean {

  @Autowired
  private UserDao userDao;

  private User defaultUser;

  protected UserDao getDao() {
    return userDao;
  }

  @Transactional(readOnly = true)
  public User getTemplateUser() {
    return userDao.get(-1L);
  }

  @Transactional(readOnly = true)
  public User findByUsername(String username) {
    return userDao.findUser(username);
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

  public User getDefaultUser() {
    return defaultUser;
  }

  public void afterPropertiesSet() throws Exception {
    defaultUser = findByUsername("default");
  }
}