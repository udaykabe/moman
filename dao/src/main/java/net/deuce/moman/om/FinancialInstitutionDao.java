package net.deuce.moman.om;

import org.hibernate.Query;


public class FinancialInstitutionDao extends EntityDao<FinancialInstitution> {

  public void clear() {
    Query query = getSession().createQuery("delete from " + FinancialInstitution.class.getName());
    deleteByQuery(query);
  }
}
