package net.deuce.moman.om;

import org.springframework.stereotype.Component;

import javax.persistence.Query;
import java.util.List;

@Component
public class TransactionDao extends UserBasedDao<InternalTransaction> {

  public List<InternalTransaction> getAccountTransactions(Account account, boolean reverse) {
    Query query = getEntityManager().createQuery(String.format("select e from %s e, %s a where e.account = a and a.id = :id order by e.externalId, e.description %s",
        InternalTransaction.class.getName(), Account.class.getName(), reverse ? "desc" : "asc"));
    query.setParameter("id", account.getId());
    return query.getResultList();
  }

  public List<InternalTransaction> getAccountEnvelopeTransactions(Account account, Envelope env, boolean reverse) {
    Query query = getEntityManager().createQuery(String.format("select distinct t from %s t, %s a, %s e, %s s where t.account = a and t.split = s and s.envelope = e and and a.id = :aid and e.id = :eid order by t.externalId, t.description %s",
        InternalTransaction.class.getName(), Envelope.class.getName(), Split.class.getName(), reverse ? "desc" : "asc"));
    query.setParameter("aid", account.getId());
    query.setParameter("eid", env.getId());
    return query.getResultList();
  }

  public InternalTransaction getInitialBalanceTransaction(Account account) {
    Query query = getEntityManager().createQuery(String.format("select e from %s e, %s a where e.account = a and a.id = :id and e.initialBalance = :bool",
        InternalTransaction.class.getName(), Account.class.getName()));
    query.setParameter("id", account.getId());
    query.setParameter("bool", true);
    return (InternalTransaction) query.getSingleResult();
  }

  public void clearCustomTransactions(User user) {
    Query query = getEntityManager().createQuery(String.format("update e set e.custom = :false from %s e, %s a, %s u where e.account = a and a.user = u and u.id = :id and e.custom = :true",
        InternalTransaction.class.getName(), Account.class.getName(), User.class.getName()));
    query.setParameter("id", user.getId());
    query.setParameter("true", true);
    query.setParameter("false", false);
  }

  public List<InternalTransaction> getCustomTransactions(User user, boolean reverse) {
    Query query = getEntityManager().createQuery(String.format("select e from %s e, %s a, %s u where e.account = a and a.user = u and u.id = :id and e.custom = :bool order by e.externalId, e.description %s",
        InternalTransaction.class.getName(), Account.class.getName(), User.class.getName(), reverse ? "desc" : "asc"));
    query.setParameter("id", user.getId());
    query.setParameter("bool", true);
    return query.getResultList();
  }

  public List<InternalTransaction> getEnvelopeTransactions(Envelope env) {
    Query query = getEntityManager().createQuery(String.format("select t from %s t, %s e, %s s where t.split = s and s.envelope = e and e.id = :id",
        InternalTransaction.class.getName(), Envelope.class.getName(), Split.class.getName()));
    query.setParameter("id", env.getId());
    return query.getResultList();
  }

  public List<InternalTransaction> getEnvelopeTransactionsForAccounts(Envelope env, List<Account> accounts) {
    if (accounts == null || accounts.size() == 0) {
      return getEnvelopeTransactions(env);
    }

    StringBuffer sql = new StringBuffer(String.format("select t from %s t, %s e, %s s, %s a where t.split = s and s.envelope = e and e.id = :id and e.account in (:accountList)",
        InternalTransaction.class.getName(), Envelope.class.getName(), Split.class.getName()));

    Query query = getEntityManager().createQuery(sql.toString());
    query.setParameter("id", env.getId());
    query.setParameter("accountList", accounts);
    return query.getResultList();
  }

}