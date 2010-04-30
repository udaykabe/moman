package net.deuce.moman.om;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class FinancialInstitutionService extends EntityService<FinancialInstitution, FinancialInstitutionDao> {

  @Autowired
  private FinancialInstitutionDao financialInstitutionDao;

  protected FinancialInstitutionDao getDao() {
    return financialInstitutionDao;
  }

  /**
   * Clears all entities.
   */
  @Transactional
  public void clear() {
    getDao().clear();
  }

  public void toXml(User user, Document doc) {

    Element root = doc.getRootElement().addElement(getRootElementName());

    for (FinancialInstitution fi : list()) {
      toXml(fi, root);
    }
  }


  public void toXml(FinancialInstitution fi, Element parent) {
    Element el = parent.addElement("financialInstitution");
    addElement(el, "id", fi.getId());
    addElement(el, "name", fi.getName());
    addElement(el, "url", fi.getUrl());
    addElement(el, "fid", fi.getFinancialInstitutionId());
    addElement(el, "org", fi.getOrganization());
  }

  public Class<FinancialInstitution> getType() {
    return FinancialInstitution.class;
  }

  public String getRootElementName() {
    return "financialInstitutions";
  }
}
