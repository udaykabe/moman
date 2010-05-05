package net.deuce.moman.controller.command;

import net.deuce.moman.job.Command;
import net.deuce.moman.om.*;
import net.deuce.moman.util.DataDateRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

public class ReflectionCommandBuilder extends BaseReflectionUtilities implements DynamicCommandBuilder, ApplicationContextAware {

  @Autowired
  private UserService userService;

  private Map<Class<AbstractEntity>, Map<String, Method>> methodMap = new HashMap<Class<AbstractEntity>, Map<String, Method>>();
  private ApplicationContext applicationContext;

  public CommandBuilderResult buildCommand(final EntityService service, String commandName, List<String> arguments) {

    Method method = getMethod(service, commandName);
    if (method == null) {
      return new CommandBuilderResult(HttpServletResponse.SC_NOT_FOUND, null, String.format("No command named '%1$s'", commandName));
    }

    int userParamPos = -1;
    for (int i = 0; userParamPos < 0 && i < method.getParameterTypes().length; i++) {
      if (method.getParameterTypes()[i].equals(User.class)) {
        userParamPos = i;
      }
    }

    int paramOffset = 0;

    if (userParamPos >= 0) paramOffset--;

    int argStringParams = method.getParameterTypes().length + paramOffset;

    if (arguments.size() != argStringParams) {
      return new CommandBuilderResult(HttpServletResponse.SC_BAD_REQUEST, null, String.format("Mismatched parameters for command '%1$s'", Arrays.asList(arguments)));
    }

    List<Object> args = new LinkedList<Object>();
    Class[] paramTypes = method.getParameterTypes();

    paramOffset = 0;

    for (int i = 0; i < paramTypes.length; i++) {

      if (userParamPos >= 0 && i == userParamPos) {
        args.add(userService.getStaticUser());
        continue;
      }

      /*
      if (dataDateRangePos >= 0 && i == dataDateRangePos) {
        String date1 = argStrings.get(i);
        String date2 = argStrings.get(i+1);
        try {
          args.add(new DataDateRange(DATE_FORMAT.parse(date1), DATE_FORMAT.parse(date2)));
          paramOffset++;
          continue;
        } catch (ParseException e) {
          errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("invalid date range date format '%1$s', '%2%s' needs to be (yyyy-MM-dd)", date1, date2));
          return;
        }
      }
      */

      Class type = paramTypes[i];
      String value = arguments.get(i + paramOffset);

      if (type.equals(Date.class) || type.equals(Timestamp.class) || type.equals(java.util.Date.class)) {
        try {
          Date date = getDateFormat().parse(value);
          args.add(date);
        } catch (ParseException e) {
          return new CommandBuilderResult(HttpServletResponse.SC_BAD_REQUEST, e, String.format("invalid date format '%1$s', needs to be (yyyy-MM-dd)", value));
        }
      } else if (type.equals(String.class)) {
        args.add(value);
      } else if (!paramTypes[i].isPrimitive() && paramTypes[i].getSuperclass().equals(AbstractEntity.class)) {
        EntityService entityService;

        if (!paramTypes[i].equals(InternalTransaction.class)) {
          entityService = (EntityService) getApplicationContext().getBean(type.getSimpleName().substring(0, 1).toLowerCase() + type.getSimpleName().substring(1) + "Service", EntityService.class);
        } else {
          entityService = (EntityService) getApplicationContext().getBean("transactionService", EntityService.class);
        }
        if (entityService == null) {
          return new CommandBuilderResult(HttpServletResponse.SC_BAD_REQUEST, null, String.format("no service found for type '%1$s'", type.getName()));
        }
        AbstractEntity entity = entityService.getByUuid(value);
        if (entity == null) {
          return new CommandBuilderResult(HttpServletResponse.SC_NOT_FOUND, null, String.format("no " + type.getName() + " entity exist with uuid '%1$s'", value));
        }
        args.add(entity);
      } else if (type.equals(DataDateRange.class)) {

      } else {
        try {
          Method valueOfMethod = type.getDeclaredMethod("valueOf", String.class);
          Object valueOf = valueOfMethod.invoke(null, value);
          args.add(valueOf);
        } catch (NoSuchMethodException e) {
          return new CommandBuilderResult(HttpServletResponse.SC_NOT_FOUND, e, String.format(type.getName() + " has no valueOf property"));
        } catch (Exception e) {
          return new CommandBuilderResult(HttpServletResponse.SC_BAD_REQUEST, e, String.format("failed to construct arg list for command '%1$s', reason: %2$s", Arrays.asList(arguments), e.getMessage()));
        }
      }
    }

    try {
      return new CommandBuilderResult((Command) method.invoke(service, args.toArray()));
    } catch (Exception e) {
      return new CommandBuilderResult(HttpServletResponse.SC_BAD_REQUEST, e, String.format("failed to execute command '%1$s', reason: %2$s", arguments, e.getMessage()));
    }

  }

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }
}
