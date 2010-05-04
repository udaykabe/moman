package net.deuce.moman.droid.model;

import net.deuce.moman.droid.HttpRequest;
import net.deuce.moman.droid.HttpRequestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.dom4j.Document;
import org.dom4j.Element;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EntityClient {

  private static final String BASE_URL = "http://10.0.2.2:9085/service/";
  protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  private Map<Class<EntityClient>, Map<String, Method>> methodMap = new HashMap<Class<EntityClient>, Map<String, Method>>();

  private Map<String, String> properties = new HashMap<String, String>();

  public EntityClient() {
  }

  public abstract String getEntityName();

  public abstract String getServiceName();

  protected String getProperty(String name) {
    return properties.get(name);
  }

  protected boolean getBoolean(String name) {
    return Boolean.valueOf(properties.get(name));
  }

  protected long getLong(String name) {
    String val = properties.get(name);
    return val != null ? Long.valueOf(properties.get(name)) : 0L;
  }

  protected int getInt(String name) {
    String val = properties.get(name);
    return val != null ? Integer.valueOf(properties.get(name)) : 0;
  }

  protected double getDouble(String name) {
    String val = properties.get(name);
    return val != null ? Double.valueOf(properties.get(name)) : 0.0;
  }

  protected Date getDate(String name) {
    try {
      String val = properties.get(name);
      return val != null ? DATE_FORMAT.parse(properties.get(name)) : null;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  protected void setProperty(String name, String value) {
    properties.put(name, value);
  }

  protected void setBoolean(String name, boolean value) {
    properties.put(name, Boolean.toString(value));
  }

  protected void setLong(String name, long value) {
    properties.put(name, Long.toString(value));
  }

  protected void setInt(String name, int value) {
    properties.put(name, Integer.toString(value));
  }

  protected void setDouble(String name, double value) {
    properties.put(name, Double.toString(value));
  }

  protected void setDate(String name, Date value) {
    properties.put(name, DATE_FORMAT.format(value));
  }

  protected EntityClient fetchEntity(Class clientType, String uuid) {
    try {
      EntityClient client = (EntityClient) clientType.newInstance();

      HttpRequest req = HttpRequest.newGetRequest(BASE_URL + client.getServiceName());
      req.addParameter("action", "2");
      req.addParameter("uuid", uuid);

      Document doc = HttpRequestUtils.executeRequest(req.buildMethod(), true, false);

      Element entity = (Element) doc.selectSingleNode("//" + client.getEntityName());
      if (entity == null) return null;

      buildEntityClient(client, entity);

      return client;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void buildEntityClient(EntityClient client, Element xml) {
    String uuid = xml.attribute("id").getText();
    client.setProperty("uuid", uuid);

    for (Element property : (List<Element>) xml.elements()) {
        String name = property.getName();

        if (property.attribute("id") != null) {
          client.setProperty(name+"Id", property.attribute("id").getText());
        } else {
          String value = property.getText();
          client.setProperty(name, value);
        }
      }
  }

}
