package net.deuce.moman.droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import net.deuce.moman.droid.model.EntityClient;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.*;
import java.net.ConnectException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public abstract class BaseActivity extends Activity {

  protected static final int MENU_QUIT = 0;
  protected static final int MENU_ACCOUNTS = 1;
  protected static final int MENU_TRANSACTIONS = 2;
  protected static final int MENU_TRANSFER = 3;
  protected static final int MENU_SERVER = 4;

  protected static final String BASE_URL_FORMAT = "http://%1$s/service/";
  protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

//  protected static String SERVER = "10.0.2.2:9086";
  protected static String SERVER = "192.168.1.198:9086";

  protected static String buildBaseUrl() {
    return String.format(BASE_URL_FORMAT, SERVER);
  }

  protected abstract void doOnCreate(Bundle savedInstanceState) throws ConnectException;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
      doOnCreate(savedInstanceState);
    } catch (ConnectException e) {
      server();
    } catch (Throwable t) {
      PrintWriter out = null;

      try {
        File root = Environment.getExternalStorageDirectory();
        if (root.canWrite()) {
          File file = new File(root + "/moman.log");
          //
          FileWriter datawriter = new FileWriter(file, true);
          out = new PrintWriter(datawriter);
          t.printStackTrace(out);
        }
      } catch (IOException e) {
        Log.e("Whoops", "Can’t write" + e.getMessage());
      } finally {
        if (out != null) {
          try {
            out.close();
          } catch (Exception e) {
            Log.e("ERROR", "Failed to close file: " + e.getMessage());
          }
        }
      }

      StringWriter sw = new StringWriter();
      t.printStackTrace(new PrintWriter(sw));
      savedInstanceState.putString("exception", sw.toString());
      Intent intent = new Intent(this, Debug.class);
      startActivity(intent);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case MENU_ACCOUNTS:
        accounts();
        return true;
      case MENU_TRANSACTIONS:
        transactions();
        return true;
      case MENU_SERVER:
        server();
        return true;
      case MENU_TRANSFER:
        transfer();
        return true;
      case MENU_QUIT:
        quit();
        return true;
    }
    return false;
  }

  protected void server() {
    Intent intent = new Intent(this, Server.class);
    startActivity(intent);
  }

  protected void accounts() {
    Intent intent = new Intent(this, Accounts.class);
    startActivity(intent);
  }

  protected void transactions() {
    Intent intent = new Intent(this, Transactions.class);
    startActivity(intent);
  }

  protected void transfer() {

  }

  protected void quit() {

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(0, MENU_ACCOUNTS, 0, "Accounts");
    menu.add(0, MENU_TRANSACTIONS, 0, "Transactions");
    menu.add(0, MENU_TRANSFER, 0, "Transfer");
    menu.add(0, MENU_SERVER, 0, "Server");
    menu.add(0, MENU_QUIT, 0, "Quit");
    return true;
  }

  protected List<EntityClient> getEntityList(Class clientType) {
    try {
      EntityClient client = (EntityClient) clientType.newInstance();
      client.getServiceName();

      HttpRequest req = HttpRequest.newGetRequest(buildBaseUrl() + client.getServiceName());
      req.addParameter("action", "3");

      Document doc = HttpRequestUtils.executeRequest(req.buildMethod(), true, false);

      List<EntityClient> list = new LinkedList<EntityClient>();

      List<Element> entities = doc.selectNodes("//" + client.getEntityName());
      if (entities == null || entities.size() == 0) return list;

      for (Element entity : entities) {
        client = (EntityClient) clientType.newInstance();
        client.buildEntityClient(client, entity);
        list.add(client);
      }

      return list;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}