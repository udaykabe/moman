package net.deuce.moman.om;

import org.hibernate.Query;

public class UserDao extends EntityDao<User> {

  public User findUser(String username) {
    Query query = getSession().createQuery(String.format("select e from %s e where e.username = :username",
        User.class.getName()));
    query.setParameter("username", username);
    return (User) query.uniqueResult();
  }
}