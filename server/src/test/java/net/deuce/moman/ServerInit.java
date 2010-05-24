package net.deuce.moman;

import org.apache.commons.httpclient.HttpClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerInit {

  private static ServerInit __instance = new ServerInit();

  public static ServerInit instance() { return __instance; }

  private ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext.xml");
  private HttpClient httpClient = new HttpClient();

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }
}
