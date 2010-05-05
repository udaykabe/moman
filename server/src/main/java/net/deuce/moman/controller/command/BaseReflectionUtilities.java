package net.deuce.moman.controller.command;

import net.deuce.moman.om.AbstractEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class BaseReflectionUtilities implements ApplicationContextAware {

  protected static final EntityResult OK = new EntityResult();

  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  private Map<Class<AbstractEntity>, Map<String, Method>> methodMap = new HashMap<Class<AbstractEntity>, Map<String, Method>>();
  private ApplicationContext applicationContext;

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  protected Method getMethod(Object source, String name) {
    Map<String, Method> map = getMethodMap(source.getClass());
    Method method = map.get(name);
    if (method == null) {
      for (Method m : source.getClass().getDeclaredMethods()) {
        if (m.getName().equals(name)) {
          method = m;
          map.put(name, m);
          break;
        }
      }
    }
    return method;
  }

  private Map<String, Method> getMethodMap(Class type) {
    Map<String, Method> m = methodMap.get(type);
    if (m == null) {
      m = new HashMap<String, Method>();
      methodMap.put(type, m);
    }
    return m;
  }

  protected Method getGetterMethodForPropertyName(Object source, String name) {
    Map<String, Method> map = getMethodMap(source.getClass());
    String key = "get" + name;
    Method method = map.get(key);
    if (method == null) {
      for (Method m : source.getClass().getDeclaredMethods()) {
        for (String prefix : new String[]{"get", "is"}) {
          if (m.getName().startsWith(prefix)) {
            int len = prefix.length();
            String getterName = m.getName().substring(len, len + 1).toLowerCase() + m.getName().substring(len + 1);
            if (getterName.equals(name)) {
              method = m;
              map.put(key, m);
              break;
            }
          }
        }
      }
    }
    return method;
  }


  protected Method getSetterMethodForPropertyName(Object source, String name) {
    Map<String, Method> map = getMethodMap(source.getClass());
    String key = "set" + name;
    Method method = map.get(key);
    if (method == null) {
      for (Method m : source.getClass().getDeclaredMethods()) {
        if (m.getName().startsWith("set")) {
          String setterName = m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4);
          if (setterName.equals(name)) {
            method = m;
            map.put(key, m);
            break;
          }
        }
      }
    }
    return method;
  }

  public DateFormat getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(DateFormat dateFormat) {
    this.dateFormat = dateFormat;
  }
}
