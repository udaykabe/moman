package net.deuce.moman.controller.command;

import net.deuce.moman.job.Command;
import net.deuce.moman.job.Result;
import net.deuce.moman.om.AbstractEntity;
import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.InternalTransaction;
import net.deuce.moman.om.User;
import net.deuce.moman.util.DataDateRange;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ExecuteCommandController extends AbstractCommandController {

  @Autowired
  private GetEntityController getEntityController;

  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

    String[] pathInfo = request.getPathInfo().split("/");

    if (pathInfo.length < 4) {
      errorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "No command given");
      return null;
    }

    List<String> command = new LinkedList<String>();

    for (int i = 3; i < pathInfo.length; i++) {
      command.add(pathInfo[i]);
    }

    executeCommand(command, request, response);

    return null;
  }

  protected void executeCommand(List<String> command, HttpServletRequest req, HttpServletResponse res) throws IOException {

    String commandName = command.get(0);
    command.remove(0);

    final EntityService service = getService(req);

    Method method = getMethod(service, commandName);
    if (method == null) {
      errorResponse(res, HttpServletResponse.SC_NOT_FOUND, String.format("No command named '%1$s'", commandName));
      return;
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

    if (command.size() != argStringParams) {
      errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("Mismatched parameters for command '%1$s'", Arrays.asList(command)));
      return;
    }

    List<Object> args = new LinkedList<Object>();
    Class[] paramTypes = method.getParameterTypes();

    paramOffset = 0;

    for (int i = 0; i < paramTypes.length; i++) {

      if (userParamPos >= 0 && i == userParamPos) {
        args.add(getUserService().getStaticUser());
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
      String value = command.get(i + paramOffset);

      if (type.equals(Date.class) || type.equals(Timestamp.class) || type.equals(java.util.Date.class)) {
        try {
          Date date = getDateFormat().parse(value);
          args.add(date);
        } catch (ParseException e) {
          errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("invalid date format '%1$s', needs to be (yyyy-MM-dd)", value));
          return;
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
          errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("no service found for type '%1$s'", type.getName()));
          return;
        }
        AbstractEntity entity = entityService.getByUuid(value);
        if (entity == null) {
          errorResponse(res, HttpServletResponse.SC_NOT_FOUND, String.format("no " + type.getName() + " entity exist with uuid '%1$s'", value));
          return;
        }
        args.add(entity);
      } else if (type.equals(DataDateRange.class)) {

      } else {
        try {
          Method valueOfMethod = type.getDeclaredMethod("valueOf", String.class);
          Object valueOf = valueOfMethod.invoke(null, value);
          args.add(valueOf);
        } catch (NoSuchMethodException e) {
          errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format(type.getName() + " has no valueOf property"));
          return;
        } catch (Exception e) {
          errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("failed to construct arg list for command '%1$s', reason: %2$s", Arrays.asList(command), e.getMessage()));
          return;
        }
      }
    }

    try {
      Object returnValue = method.invoke(service, args.toArray());
      if (returnValue != null) {

        if (returnValue instanceof Command) {
          Command cmd = (Command) returnValue;

          Result result = getUndoManager().execute(getUserService().getStaticUser(), cmd, null);

          Document doc = buildResponse();
          doc.getRootElement().add(result.getResult());
          sendResponse(res, doc);
        } else {
          String value;
          if (returnValue instanceof Date) {
            value = getDateFormat().format(returnValue);
          } else if (returnValue instanceof AbstractEntity) {
            getEntityController.getEntity(((AbstractEntity) returnValue).getUuid(), req, res);
            return;
          } else {
            value = returnValue.toString();
          }

          Document doc = buildResponse();
          doc.getRootElement().addElement("return-value").addAttribute("value", value);
          sendResponse(res, doc);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("failed to execute command '%1$s', reason: %2$s", Arrays.asList(command), e.getMessage()));
    }

  }
}
