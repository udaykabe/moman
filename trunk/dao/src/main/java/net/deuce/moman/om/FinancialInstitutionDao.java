package net.deuce.moman.om;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;

@Component
public class FinancialInstitutionDao extends EntityDao<FinancialInstitution> {

  @Transactional
  public void clear() {
    Query query = getEntityManager().createQuery("delete from " + FinancialInstitution.class.getName());
    deleteByQuery(query);
  }
}
