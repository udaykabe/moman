package net.deuce.moman.controller.command;

import net.deuce.moman.om.AbstractEntity;
import net.deuce.moman.om.EntityService;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

public class ReflectionEntityAdapter extends BaseReflectionUtilities implements EntityAdapter {

  public EntityResult getProperty(EntityService service, String uuid, String property) {

    AbstractEntity entity = service.findEntity(uuid);

    if (entity == null) {
      return new EntityResult(HttpServletResponse.SC_NOT_FOUND, null, String.format("No %1$s exists with uuid = '%2$s'", service.getEntityClass().getSimpleName(), uuid));
    }

    Method method = getGetterMethodForPropertyName(entity, property);
    if (method == null) {
      return new EntityResult(HttpServletResponse.SC_BAD_REQUEST, null, String.format(service.getType().getSimpleName() + " has no property '%1$s'", property));
    }

    Object result = null;
    try {
      return new EntityResult(method.invoke(entity));
    } catch (Exception e) {
      return new EntityResult(HttpServletResponse.SC_BAD_REQUEST, e, String.format("failed to get '%1$s' property: %2$s", property, e.getMessage()));
    }

  }

  public EntityResult setProperty(EntityService service, String uuid, String property, Object value) {
    return null;
  }

  public EntityResult getProperty(EntityService service, AbstractEntity entity, String name) {

    Method method = getGetterMethodForPropertyName(entity, name);
    if (method == null) {
      return new EntityResult(HttpServletResponse.SC_BAD_REQUEST, null, String.format(service.getType().getSimpleName() + " has no property '%1$s'", name));
    }

    Object result = null;
    try {
      result = method.invoke(entity);
    } catch (Exception e) {
      return new EntityResult(HttpServletResponse.SC_BAD_REQUEST, e, String.format("failed to get '%1$s' property: %2$s", name, e.getMessage()));
    }

    if (result == null) {
      return new EntityResult(AbstractCommandController.NULL);
    } else if (result instanceof Date) {
      return new EntityResult(getDateFormat().format((Date) result));
    } else {
      return new EntityResult(result.toString());
    }
  }

  public EntityResult setProperty(EntityService service, AbstractEntity entity, String name, String value) {

    Method method = getSetterMethodForPropertyName(entity, name);
    if (method == null) {
      return new EntityResult(HttpServletResponse.SC_BAD_REQUEST, null, String.format(service.getType().getSimpleName() + " has no property '%1$s'", name));
    }

    Class type = method.getParameterTypes()[0];
    if (type.equals(Date.class) || type.equals(Timestamp.class) || type.equals(java.util.Date.class)) {
      try {
        Date date = getDateFormat().parse(value);
        method.invoke(entity, date);
      } catch (ParseException e) {
        return new EntityResult(HttpServletResponse.SC_BAD_REQUEST, e, String.format("invalid date format '%1$s', needs to be (yyyy-MM-dd)", value));
      } catch (Exception e) {
        return new EntityResult(HttpServletResponse.SC_BAD_REQUEST, e, String.format("failed to set Date '%1$s' property with value '%2$s': %3$s", name, value, e.getMessage()));
      }
    } else if (type.equals(String.class)) {
      try {
        method.invoke(entity, value);
      } catch (Exception e) {
        return new EntityResult(HttpServletResponse.SC_BAD_REQUEST, e, String.format("failed to set String '%1$s' property with value '%2$s': %3$s", name, value, e.getMessage()));
      }
    } else {
      try {
        Method valueOfMethod = type.getDeclaredMethod("valueOf", String.class);
        Object valueOf = valueOfMethod.invoke(null, value);
        method.invoke(entity, valueOf);
      } catch (NoSuchMethodException e) {
        return new EntityResult(HttpServletResponse.SC_BAD_REQUEST, e, String.format(type.getName() + " has no valueOf property", name));
      } catch (Exception e) {
        return new EntityResult(HttpServletResponse.SC_BAD_REQUEST, e, String.format("failed to set '%1$s' property with value '%2$s': %3$s", name, value, e.getMessage()));
      }
    }

    return OK;
  }

}
