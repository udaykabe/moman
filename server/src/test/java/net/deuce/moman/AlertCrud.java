package net.deuce.moman;

import net.deuce.moman.om.Account;
import net.deuce.moman.om.Alert;
import net.deuce.moman.om.AlertType;
import org.junit.Before;

public class AlertCrud extends EntityCrud<Alert> {

  private EnvelopeCrud envelopeCrud = new EnvelopeCrud();
  private TransactionCrud transactionCrud = new TransactionCrud();
  private String envelopeUuid;
  private String transactionUuid;

  @Override
  protected void setup() throws Exception {
    super.setup();
    envelopeUuid = envelopeCrud.createEntity();
    transactionUuid = transactionCrud.createEntity();
  }

  @Override
  protected void teardown() throws Exception {
    super.teardown();
    envelopeCrud.deleteEntity("envelope", envelopeUuid);
    transactionCrud.deleteEntity("transaction", envelopeUuid);
  }

  @Override
  protected String getCollectionName() {
    return "alerts";
  }

  @Override
  protected String getEntityName() {
    return "alert";
  }

  @Override
  protected String getCreatePath() {
    return "/envelope/" + envelopeUuid + "/transaction/" + transactionUuid + "/alertType/" + AlertType.UNRESOLVED;
  }

  @Override
  protected String getEditPropertyName() {
    return "nickname";
  }

  @Override
  protected String getEditPropertyValue() {
    return "New Nickname";
  }
}