package net.deuce.moman.om;

import net.deuce.moman.util.Constants;
import net.deuce.moman.util.Utils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService extends UserBasedService<InternalTransaction, TransactionDao> {

  @Autowired
  private TransactionDao transactionDao;

  protected TransactionDao getDao() {
    return transactionDao;
  }

  public void buildTransactionXml(Element el, InternalTransaction trans) {
    el.addAttribute("id", trans.getUuid());
    el.addElement("account").addAttribute("id", trans.getAccount().getUuid());
    addElement(el, "amount", Utils.formatDouble(trans.getAmount()));
    addElement(el, "type", trans.getType().name());
    if (trans.getDate() != null) {
      addElement(el, "date", Constants.DATE_FORMAT.format(trans.getDate()));
    }
    addElement(el, "desc", trans.getDescription());
    addOptionalElement(el, "extid", trans.getExternalId());
    addOptionalBooleanElement(el, "initial-balance", trans.isInitialBalance());
    addOptionalElement(el, "balance", Utils.formatDouble(trans.getBalance()));
    addElement(el, "memo", trans.getMemo());
    addElement(el, "check", trans.getCheckNo());
    addElement(el, "ref", trans.getRef());
    addElement(el, "status", trans.getStatus().name());
    if (trans.getTransferTransaction() != null) {
      el.addElement("etransfer").addAttribute("id", trans.getTransferTransaction().getUuid());
    }
    Element sel = el.addElement("split");
    Element eel;
    for (Split item : trans.getSplit()) {
      eel = sel.addElement("envelope");
      eel.addAttribute("id", item.getEnvelope().getUuid());
      eel.addAttribute("amount", Utils.formatDouble(item.getAmount()));
    }
  }

  public void toXml(User user, Document doc) {

    Element root = doc.getRootElement().addElement("transactions");

    for (InternalTransaction trans : getEntities(user)) {
      toXml(trans, root);
    }
  }

  public void toXml(InternalTransaction trans, Element parent) {
    Element el = parent.addElement("transaction");
    buildTransactionXml(el, trans);
  }

  public Class<InternalTransaction> getType() {
    return InternalTransaction.class;
  }

  public String getRootElementName() {
    return "transactions";
  }


}