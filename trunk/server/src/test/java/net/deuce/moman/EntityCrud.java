package net.deuce.moman;

import net.deuce.moman.om.AbstractEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public abstract class EntityCrud<E extends AbstractEntity> extends BaseTest {

  private boolean needsSetup = true;

  protected abstract String getCollectionName();

  protected abstract String getEntityName();

  protected abstract String getCreatePath() throws UnsupportedEncodingException;

  protected abstract String getEditPropertyName();

  protected abstract String getEditPropertyValue();

  protected void setup() throws Exception {
    needsSetup = false;
  }

  protected void teardown() throws Exception {
  }

  public void execute() throws Exception {
    setup();
    String entityName = getEntityName();
    Document doc = listEntities(getCollectionName(), entityName, -1);
//    dumpElement(doc.getRootElement());

    int initialEntities = doc.selectNodes("/moman/" + getCollectionName() + "/" + entityName).size();
    String uuid = createEntity();
    editEntity(uuid);
    deleteEntity(entityName, uuid);
    listEntities(getCollectionName(), entityName, initialEntities);
    teardown();
  }

  public String createEntity() throws Exception {
    boolean needsTearDown = needsSetup;
    if (needsSetup) {
      setup();
    }
    GetMethod method = new GetMethod("http://localhost:10085/service/" + getEntityName() + "/new" + getCreatePath());
    Document doc = executeRequest(method, false);
//    dumpElement(doc.getRootElement());
    String uuid = doc.selectSingleNode("/moman/" + getCollectionName() + "/" + getEntityName() + "/@id").getText();
    getEntity(getCollectionName(), getEntityName(), uuid);

    if (needsTearDown) {
      teardown();
    }
    return uuid;
  }

  public void editEntity(String uuid) throws Exception {
    GetMethod method = new GetMethod("http://localhost:10085/service/" + getEntityName() + "/edit/" + uuid + "/" + getEditPropertyName() + "/" + URLEncoder.encode(getEditPropertyValue(), "UTF-8"));
    Document doc = executeRequest(method, false);
//    dumpElement(doc.getRootElement());
    String newValue = doc.selectSingleNode("/moman/" + getCollectionName() + "/" + getEntityName() + "/" + getEditPropertyName()).getText();
    Assert.assertEquals("Entity edit failed", newValue, getEditPropertyValue());

    doc = getEntity(getCollectionName(), getEntityName(), uuid);
    Assert.assertEquals("Entity edit failed", getEditPropertyValue(), doc.selectSingleNode("/moman/" + getCollectionName() + "/" + getEntityName() + "/" + getEditPropertyName()).getText());
  }

}
