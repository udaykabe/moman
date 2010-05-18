package net.deuce.moman.om;

import org.hibernate.Query;

public class EnvelopeDao extends UserBasedDao<Envelope> {

  private Envelope getSpecialEnvelope(User user, String attr) {
    Query query = getSession().createQuery(String.format("select e from %s e e.user = :user and e.%s = :bool",
        Envelope.class.getName(), attr));
    query.setParameter("user", user);
    query.setParameter("bool", true);
    return (Envelope) query.uniqueResult();
  }

  public Envelope getRootEnvelope(User user) {
    return getSpecialEnvelope(user, "root");
  }

  public Envelope getAvailableEnvelope(User user) {
    return getSpecialEnvelope(user, "available");
  }

  public Envelope getMonthlyEnvelope(User user) {
    return getSpecialEnvelope(user, "monthly");
  }

  public Envelope getUnassignedEnvelope(User user) {
    return getSpecialEnvelope(user, "unassigned");
  }

  public Envelope getSavingsGoalsEnvelope(User user) {
    return getSpecialEnvelope(user, "savingsGoals");
  }

}