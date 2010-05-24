package net.deuce.moman;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.Assert;
import java.io.InputStream;

public class BaseTest {

  public Document getEntity(String collectionName, String entity, String uuid) throws Exception {
    GetMethod method = new GetMethod("http://localhost:10085/service/" + entity + "/get/" + uuid);
    Document doc = executeRequest(method, false);
    Assert.assertEquals("entity get failed", uuid, doc.selectSingleNode("/moman/"+collectionName+"/"+entity+"/@id").getText());
    return doc;
  }

  public Document deleteEntity(String entity, String uuid) throws Exception {
    GetMethod method = new GetMethod("http://localhost:10085/service/" + entity + "/delete/" + uuid);
    return executeRequest(method, true);
  }

  public Document listEntities(String collectionName, String entity, int expectedCount) throws Exception {
    GetMethod method = new GetMethod("http://localhost:10085/service/"+entity+"/list");
    Document doc = executeRequest(method, false);
    if (expectedCount >= 0) {
      Assert.assertEquals("entity list failed", expectedCount, doc.selectNodes("/moman/"+collectionName+"/"+entity).size());
    }
    return doc;
  }

  public Document executeRequest(HttpMethod method, boolean ignore) throws Exception {
    HttpMethod redirect = null;
    InputStream contentStream = null;

    try {
      ServerInit.instance().getHttpClient().executeMethod(method);
      int statusCode = method.getStatusCode();
      if (statusCode == HttpStatus.SC_OK) {
        contentStream = method.getResponseBodyAsStream();
      } else if ((statusCode == HttpStatus.SC_MOVED_TEMPORARILY) ||
          (statusCode == HttpStatus.SC_MOVED_PERMANENTLY) ||
          (statusCode == HttpStatus.SC_SEE_OTHER) ||
          (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
        Header header = method.getResponseHeader("location");
        if (header != null) {
          String newuri = header.getValue();
          if ((newuri == null) || (newuri.equals(""))) {
            newuri = "/";
          }
          System.out.println("Redirect target: " + newuri);
          redirect = new GetMethod(newuri);

          ServerInit.instance().getHttpClient().executeMethod(redirect);
          contentStream = redirect.getResponseBodyAsStream();
        } else {
          String msg = "Invalid redirect: " + method.getURI();
          throw new RuntimeException(msg);
        }
      } else {
        String msg = "Status code (" + statusCode + ") not handled for: " + method.getURI();
        throw new RuntimeException(msg);
      }

      Document document = null;
      try {
        SAXReader reader = new SAXReader();
        document = reader.read(contentStream);
        if (document != null && document.selectSingleNode("//error") != null) {
          dumpElement(document.getRootElement());
        }
      } catch (DocumentException de) {
        if (!ignore) {
          de.printStackTrace();
        }
      }

      if (!ignore) {
        return document;
      }

    } finally {
      //if (contentStream != null) contentStream.close();
      Exception exception = null;
      if (method != null) try {
        method.releaseConnection();
      } catch (Exception e) {
        exception = e;
      }
      if (redirect != null) redirect.releaseConnection();

      if (exception != null) {
        throw new RuntimeException(exception);
      }
    }
    return null;
  }

  protected void dumpElement(Element e) {
    OutputFormat format = OutputFormat.createPrettyPrint();
    XMLWriter writer = null;
    try {
      writer = new XMLWriter(System.out, format);
      writer.write(e);
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }
}
