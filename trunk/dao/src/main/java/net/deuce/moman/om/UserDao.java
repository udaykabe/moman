package net.deuce.moman.om;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;

@Component
public class UserDao extends EntityDao<User> {

  public User findUser(String username) {
    Query query = getEntityManager().createQuery(String.format("select e from %s e where e.username = :username",
        User.class.getName()));
    query.setParameter("username", username);
    return (User) query.getSingleResult();
  }
}