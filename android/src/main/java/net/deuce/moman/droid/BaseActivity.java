package net.deuce.moman.droid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import net.deuce.moman.client.model.EnvelopeClient;
import net.deuce.moman.client.service.NoAvailableServerException;
import net.deuce.moman.util.Utils;

import java.io.*;
import java.net.ConnectException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public abstract class BaseActivity extends Activity {

  protected static final int MENU_ACCOUNTS = 1;
  protected static final int MENU_TRANSACTIONS = 2;
  protected static final int MENU_TRANSFER = 3;
  protected static final int MENU_SERVER = 4;
  protected static final int MENU_IMPORT = 5;

  protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  protected abstract void doOnCreate(Bundle savedInstanceState) throws ConnectException, NoAvailableServerException;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
      doOnCreate(savedInstanceState);
    } catch (NoAvailableServerException e) {
      server();
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

      t.printStackTrace();
//      StringWriter sw = new StringWriter();
//      t.printStackTrace(new PrintWriter(sw));
//      savedInstanceState.putString("exception", sw.toString());
//      Intent intent = new Intent(this, Debug.class);
//      startActivity(intent);
    }
  }

  @Override
  public boolean onOptionsItemSelected
      (MenuItem
          item) {
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
      case MENU_IMPORT:
        importBankTransactions();
        return true;
    }
    return false;
  }

  private void importBankTransactions() {
    Intent intent = new Intent(this, ImportTransactions.class);
    startActivity(intent);
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
    Intent intent = new Intent(this, EnvelopeTransfer.class);
    startActivity(intent);
  }

  protected String getEnvelopeLabel(EnvelopeClient env) {
    return env.getName() + " (" + Utils.CURRENCY_FORMAT.format(env.getBalance()) + ")";
  }

  protected boolean showAccountsMenuItem() {
    return true;
  }

  protected boolean showTransactionsMenuItem() {
    return true;
  }

  protected boolean showImportMenuItem() {
    return true;
  }

  protected boolean showTransferMenuItem() {
    return true;
  }

  protected boolean showServerMenuItem() {
    return true;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (showAccountsMenuItem()) menu.add(0, MENU_ACCOUNTS, 0, "Accounts");
    if (showTransactionsMenuItem()) menu.add(0, MENU_TRANSACTIONS, 0, "Transactions");
    if (showImportMenuItem()) menu.add(0, MENU_IMPORT, 0, "Import");
    if (showTransferMenuItem()) menu.add(0, MENU_TRANSFER, 0, "Transfer");
    if (showServerMenuItem()) menu.add(0, MENU_SERVER, 0, "Server");
    return true;
  }

}