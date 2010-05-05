package net.deuce.moman.controller.command;

import net.deuce.moman.om.AbstractEntity;
import net.deuce.moman.om.EntityService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

public interface EntityAdapter {

  public EntityResult getProperty(EntityService service, String uuid, String property);
  public EntityResult setProperty(EntityService service, String uuid, String property, Object value);

  public EntityResult getProperty(EntityService service, AbstractEntity entity, String property);
  public EntityResult setProperty(EntityService service, AbstractEntity entity, String property, String value);
}
