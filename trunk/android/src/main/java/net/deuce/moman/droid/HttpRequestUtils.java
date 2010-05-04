package net.deuce.moman.droid;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.ccil.cowan.tagsoup.Parser;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class HttpRequestUtils {
  private static SAXTransformerFactory saxTransformerFactory = null;

  private static HttpClient httpClient;

  static {
    httpClient = new HttpClient();
  }

  //private static Logger log = Logger.getLogger(HttpRequestUtils.class.getName());

  public static GetMethod buildRequestMethod(String req, Map<String, String> headers)
      throws Exception {
    String[] split = req.split("\\?");
    GetMethod method = new GetMethod(split[0]);

    if (split.length > 1) {
      List<NameValuePair> params = new LinkedList<NameValuePair>();
      for (String param : split[1].split("&")) {
        String[] nameValueSplit = param.split("=");
        if (nameValueSplit.length > 1) {
          params.add(new NameValuePair(nameValueSplit[0], nameValueSplit[1]));
        } else {
          params.add(new NameValuePair(nameValueSplit[0], ""));
        }
      }
      if (params.size() > 0) {
        method.setQueryString(params.toArray(new NameValuePair[params.size()]));
      }
    }
    if (headers != null) {
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        method.addRequestHeader(entry.getKey(), entry.getValue());
      }
    }
    return method;
  }

  public static Document executeRequest(HttpRequest req, boolean ignoreResponse)
      throws Exception {
    //log.info("Sending request: " + req);
    Document doc = executeRequest(req.buildMethod(), req.isXml(), ignoreResponse);
    // ZZZ
    StringWriter sw = new StringWriter();
    OutputFormat format = OutputFormat.createPrettyPrint();
    XMLWriter writer = new XMLWriter(new PrintWriter(sw), format);
    writer.write(doc);
    //log.info("Response:");
    //log.info(sw.toString());
    return doc;
  }

  public static Document executeRequest(HttpMethod method, boolean xml, boolean ignoreResponse) throws Exception {
    HttpMethod redirect = null;
    InputStream contentStream = null;

    try {
      httpClient.executeMethod(method);
      int statusCode = method.getStatusCode();
      if (statusCode == HttpStatus.SC_OK) {
        if (!ignoreResponse) {
          contentStream = method.getResponseBodyAsStream();
        }
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

          httpClient.executeMethod(redirect);
          if (!ignoreResponse) {
            contentStream = redirect.getResponseBodyAsStream();
          }
        } else {
          String msg = "Invalid redirect: " + method.getURI();
          throw new RuntimeException(msg);
        }
      } else {
        String msg = "Status code (" + statusCode + ") not handled for: " + method.getURI();
        throw new RuntimeException(msg);
      }

      if (!ignoreResponse) {
        if (!xml) {
          DocumentResult documentResult = new DocumentResult();
          Parser p = new Parser();
          p.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
          TransformerHandler transformerHandler = getSaxTFactory().newTransformerHandler();
          p.setContentHandler(transformerHandler);
          transformerHandler.setResult(documentResult);
          p.parse(new InputSource(contentStream));
          return documentResult.getDocument();
        } else {
          SAXReader reader = new SAXReader();
          try {
            Document document = reader.read(contentStream);
            return document;
          } catch (DocumentException de) {
            return executeRequest(method, false, false);
          }
        }
      }

      return null;
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
        throw exception;
      }
    }
  }

  private static SAXTransformerFactory getSaxTFactory() {
    if (saxTransformerFactory == null) {
      TransformerFactory tFactory = TransformerFactory.newInstance();
      if (tFactory instanceof SAXTransformerFactory) {
        saxTransformerFactory = ((SAXTransformerFactory) tFactory);
      } else {
        throw new RuntimeException(
            "TransformerFactory implementation needs to implement SAXTransformerFactory.");
      }
    }
    return saxTransformerFactory;
  }
}
