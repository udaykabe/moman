package net.deuce.moman.client.service;

import net.deuce.moman.client.HttpRequest;
import net.deuce.moman.client.HttpRequestUtils;
import net.deuce.moman.client.model.EntityClient;
import net.deuce.moman.client.service.NoAvailableServerException;
import org.dom4j.Document;
import org.dom4j.Element;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public abstract class EntityClientService<E extends EntityClient> {

  private static final String BASE_URL_FORMAT = "http://%1$s/service";
  private static String LOCALHOST = "10.0.2.2:9085";
  private static String HOME = "192.168.1.198:9086";
  private static String WORK = "172.22.2.164:9087";
  private static String SERVER = null;

  private static String[] PREFERRED_SERVERS = {LOCALHOST, HOME, WORK};

  protected abstract String getServiceName();

  protected abstract String getEntityName();

  protected abstract E newEntity();

  public static String getServer() {
    return SERVER;
  }

  public static void setServer(String server) {
    SERVER = server;
  }

  protected String buildBaseUrl() throws NoAvailableServerException {
    if (SERVER == null) {
      findPreferredServer();
    }
    StringBuffer sb = new StringBuffer(String.format(BASE_URL_FORMAT, SERVER));
    return sb.toString();
  }

  protected String buildServiceUrl(String... args) throws NoAvailableServerException {
    StringBuffer sb = new StringBuffer(buildBaseUrl());
    for (String s : args) {
      sb.append('/').append(s);
    }
    return sb.toString();
  }

  private void findPreferredServer() throws NoAvailableServerException {

    String foundServer = null;
    for (String s : PREFERRED_SERVERS) {
      try {
        InetAddress address = InetAddress.getByName(s.split(":")[0]);
        if (address.isReachable(2000)) {
          SERVER = s;
          return;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    throw new NoAvailableServerException();
  }

  public List<E> list(Comparator<E> sort) throws NoAvailableServerException {
    try {
      HttpRequest req = HttpRequest.newGetRequest(buildServiceUrl(new String[]{getServiceName(), "list"}));

      Document doc = HttpRequestUtils.executeRequest(req.buildMethod(), true, false);


      List<E> list = new LinkedList<E>();

      List<Element> entities = doc.selectNodes("//" + getEntityName());
      if (entities == null || entities.size() == 0) return list;

      E client;
      for (Element entity : entities) {
        client = newEntity();
        client.buildEntityClient(client, entity);
        list.add(client);
      }

      if (sort != null) {
        Collections.sort(list, sort);
      }

      return list;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
