package net.deuce.moman;

import org.mortbay.jetty.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;

public class Main {
  public static void main(String[] args) {

    Logger log = LoggerFactory.getLogger(Main.class);
    String applicationContextPath = "/applicationContext.xml";

    Server server = null;
    boolean error = false;

    try {

      Resource config = new ClassPathResource(applicationContextPath);
      XmlBeanFactory bf = new XmlBeanFactory(new UrlResource(config.getURL()));
      server = (Server) bf.getBean("WebServer", Server.class);
      server.join();
    } catch (IOException e) {
      log.error("Failed opening application context: " + applicationContextPath, e);
      error = true;
    } catch (InterruptedException e) {
      log.error("Server was interupted", e);
      error = true;
    } catch (Exception e) {
      log.error("Error launching moman", e);
      error = true;
    }

    if (error && server != null) {
      try {
        server.stop();
        server.destroy();
      } catch (Exception e) {
        log.error("Error while stopping server", e);
      }
    }
  }
}
