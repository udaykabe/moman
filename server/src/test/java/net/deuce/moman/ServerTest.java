package net.deuce.moman;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.net.URLEncoder;

public class ServerTest {

  @Before
  public void setup() {
//    ServerInit.instance();
  }

  @Test
  public void crudTests() throws Exception {

    EntityCrud[] cruds = {
        new UserCrud(),
        new AccountCrud(),
        new EnvelopeCrud(),
        new TransactionCrud(),
        new AlertCrud(),
        new AllocationCrud(),
        new BillCrud(),
        new DeviceCrud(),
        new IncomeCrud(),
        new PayeeCrud(),
        new PreferenceCrud(),
        new RepeatingTransactionCrud(),
        new RuleCrud(),
        new SavingsGoalCrud(),
        new TagCrud(),
    };

    for (EntityCrud crud : cruds) {
//      crud.execute();
    }
  }

}
