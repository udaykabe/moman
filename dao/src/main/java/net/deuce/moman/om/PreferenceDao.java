package net.deuce.moman.om;

import org.hibernate.Query;

public class PreferenceDao extends UserBasedDao<Preference> {

  public Preference getPreferenceByName(User user, String name) {
    Query query = getSession().createQuery(String.format("select e from %s e where e.user = :user and e.name = :name",
        Preference.class.getName(), name));
    query.setParameter("user", user);
    query.setParameter("name", name);
    return (Preference) query.uniqueResult();
  }
}