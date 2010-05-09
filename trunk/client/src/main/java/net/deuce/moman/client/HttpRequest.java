package net.deuce.moman.client;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
  private String url;
  private HttpMethodType method = HttpMethodType.GET;
  private Map<String, String> parameters = new HashMap<String, String>();
  private Map<String, String> variables = new HashMap<String, String>();
  private Map<String, String> headers = new HashMap<String, String>();
  private boolean containsSections = false;
  private boolean xml;

  public enum HttpMethodType {
    GET, POST;
  }

  public static HttpRequest newGetRequest(String url) {
    return new HttpRequest(url, HttpMethodType.GET);
  }

  public static HttpRequest newPostRequest(String url) {
    return new HttpRequest(url, HttpMethodType.POST);
  }

  public HttpRequest(String url, HttpMethodType method) {
    this.url = url;
    this.method = method;
  }

  public Map<String, String> getVariables() {
    return variables;
  }

  public void setVariables(Map<String, String> variables) {
    this.variables = variables;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public HttpMethodType getMethod() {
    return method;
  }

  public void setMethod(HttpMethodType method) {
    this.method = method;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, String> parameters) {
    parameters.clear();
    for (Map.Entry<String, String> entry : parameters.entrySet()) {
      addParameter(entry.getKey(), entry.getValue());
    }
  }

  public void addParameter(String name, String value) {
    try {
      parameters.put(name, URLEncoder.encode(value, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public void addVariable(String name, String value) {
    variables.put(name, value);
  }

  public void addHeader(String name, String value) {
    headers.put(name, value);
  }

  public String substituteVariable(String value) {
    if (variables == null) return value;

    for (Map.Entry<String, String> entry : variables.entrySet()) {
      value = value.replaceAll("%" + entry.getKey() + "%", entry.getValue());
    }
    return value;
  }

  public HttpMethod buildMethod() {
    HttpMethod httpMethod;
    if (HttpMethodType.POST.equals(method)) {
      httpMethod = new PostMethod(url);
      PostMethod post = (PostMethod) httpMethod;
      for (Map.Entry<String, String> entry : parameters.entrySet()) {
        post.addParameter(entry.getKey(), substituteVariable(entry.getValue()));
      }
    } else {
      httpMethod = new GetMethod(url);
      StringBuffer queryString = new StringBuffer();
      if (httpMethod.getQueryString() != null) {
        queryString.append(httpMethod.getQueryString());
      }
      for (Map.Entry<String, String> entry : parameters.entrySet()) {
        if (queryString.length() > 0) {
          queryString.append('&');
        }
        queryString.append(entry.getKey()).append('=').append(substituteVariable(entry.getValue()));
      }
      httpMethod.setQueryString(queryString.toString());
    }
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      Header header = new Header(entry.getKey(), entry.getValue());
      httpMethod.setRequestHeader(header);
    }
    return httpMethod;
  }

  public boolean isXml() {
    return xml;
  }

  public void setXml(boolean xml) {
    this.xml = xml;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }

  public boolean isContainsSections() {
    return containsSections;
  }

  public void setContainsSections(boolean containsSections) {
    this.containsSections = containsSections;
  }

  public String getFilename() {
    StringBuffer sb = new StringBuffer();
    for (Map.Entry<String, String> entry : parameters.entrySet()) {
      if (sb.length() == 0) {
        sb.append(url).append('.');
      } else {
        sb.append('.');
      }
      sb.append(entry.getKey()).append('.').append(substituteVariable(entry.getValue()));
    }
    return sb.toString().replaceAll("/", ".");
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (Map.Entry<String, String> entry : parameters.entrySet()) {
      if (sb.length() == 0) {
        sb.append(url).append('?');
      } else {
        sb.append('&');
      }
      sb.append(entry.getKey()).append('=').append(substituteVariable(entry.getValue()));
    }
    if (sb.length() == 0) sb.append(url);
    return "HttpRequest{" +
        "url='" + sb.toString() + '\'' +
        ", method='" + method + '\'' +
        ", xml='" + xml + '\'' +
        ", headers=" + headers +
        ", variables=" + variables +
        '}';
  }
}
