package net.deuce.moman;

import net.deuce.moman.om.InternalTransaction;
import net.deuce.moman.util.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;


public class TransactionCrud extends EntityCrud<InternalTransaction> {

  private AccountCrud accountCrud = new AccountCrud();
  private String accountUuid;

  @Override
  protected void setup() throws Exception {
    super.setup();
    accountUuid = accountCrud.createEntity();
    System.out.println("ZZZ accountUuid: " + accountUuid);
  }

  @Override
  protected void teardown() throws Exception {
    super.teardown();
    accountCrud.deleteEntity("account", accountUuid);
  }

  @Override
  protected String getCollectionName() {
    return "transactions";
  }

  @Override
  protected String getEntityName() {
    return "transaction";
  }

  @Override
  protected String getCreatePath() throws UnsupportedEncodingException {
    String[] properties = {
        "externalId", "0000",
        "amount", URLEncoder.encode("-35.0", "UTF-8"),
        "date", Constants.SHORT_DATE_FORMAT.format(new Date()),
        "description", URLEncoder.encode("TEST DESCRIPTION", "UTF-8"),
        "memo", URLEncoder.encode("TEST MEMO", "UTF-8"),
        "ref", URLEncoder.encode("TEST REF", "UTF-8"),
        "account", accountUuid,
    };

    StringBuffer sb = new StringBuffer();
    for (String s : properties) {
      sb.append('/').append(s);
    }
    return sb.toString();
  }

  @Override
  protected String getEditPropertyName() {
    return "description";
  }

  @Override
  protected String getEditPropertyValue() {
    return "New Description";
  }
}