package net.deuce.moman.om;

import net.deuce.moman.util.Constants;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepeatingTransactionService extends UserBasedService<RepeatingTransaction, RepeatingTransactionDao> {

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private RepeatingTransactionDao repeatingTransactionDao;

  protected RepeatingTransactionDao getDao() {
    return repeatingTransactionDao;
  }

  protected void buildTransactionXml(Element el, RepeatingTransaction trans) {
    transactionService.buildTransactionXml(el, trans);
    RepeatingTransaction repeatingTransaction = (RepeatingTransaction) trans;
    addElement(el, "enabled", repeatingTransaction.getEnabled());
    addElement(el, "date-due", Constants.DATE_FORMAT.format(repeatingTransaction.getDateDue()));
    addElement(el, "original-date-due", Constants.DATE_FORMAT.format(repeatingTransaction.getOriginalDateDue()));
    addElement(el, "frequency", repeatingTransaction.getFrequency().name());
    addElement(el, "count", repeatingTransaction.getCount().toString());
  }

  public void toXml(User user, Document doc) {

    Element root = doc.getRootElement().addElement(getRootElementName());

    for (RepeatingTransaction trans : getEntities(user)) {
      toXml(trans, root);
    }
  }

  public void toXml(RepeatingTransaction entity, Element parent) {
    Element el = parent.addElement("transaction");
    buildTransactionXml(el, entity);
  }

  public Class<RepeatingTransaction> getType() {
    return RepeatingTransaction.class;
  }

  public String getRootElementName() {
    return "repeating-transactions";
  }

}