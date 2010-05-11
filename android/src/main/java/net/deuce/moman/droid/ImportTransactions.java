package net.deuce.moman.droid;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import net.deuce.moman.client.model.AccountClient;
import net.deuce.moman.client.model.TransactionClient;
import net.deuce.moman.client.service.AccountClientService;
import net.deuce.moman.client.service.FinancialInstitutionClientService;
import net.deuce.moman.client.service.NoAvailableServerException;
import net.deuce.moman.client.service.TransactionListResult;
import net.deuce.moman.util.Utils;

import java.util.LinkedList;
import java.util.List;

public class ImportTransactions extends BaseActivity {

  private AccountClientService clientService = AccountClientService.instance();
  private FinancialInstitutionClientService fiClientService = FinancialInstitutionClientService.instance();

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) throws NoAvailableServerException {

   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    ScrollView sv = new ScrollView(this);
    LinearLayout ll = new LinearLayout(this);
    ll.setOrientation(LinearLayout.VERTICAL);
    sv.addView(ll);

    displayTransactions(ll);

    setContentView(sv);
  }

  private void displayTransactions(LinearLayout ll) throws NoAvailableServerException {

    TableLayout table = new TableLayout(this);
    LayoutUtils.Layout.WidthFill_HeightFill.applyViewGroupParams(table);

    // set which column is expandable/can grow
//    table.setColumnStretchable(1, true);

    // apply layout animation
    AnimUtils.setLayoutAnim_slideupfrombottom(table, this);

    List<TransactionClient> transactions = new LinkedList<TransactionClient>();

    for (AccountClient account : clientService.list(null)) {
      if (account.isSelected()) {
        transactions.addAll(fiClientService.downloadBankTransactions(account));
      }
    }

    for (TransactionClient trans : transactions) {
      TableRow row = createRow(trans);
      LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableLayoutParams(row);
      row.setPadding(2, 2, 2, 2);
      table.addView(row);
    }

    ll.addView(table);

  }

  public TableRow createRow(TransactionClient trans) {
    TableRow row = new TableRow(this);

    TextView date = new TextView(this);
    date.setText(DATE_FORMAT.format(trans.getDate()));
    date.setPadding(2, 2, 2, 2);
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(date);

    TextView desc = new TextView(this);
    desc.setText(trans.getDescription());
    desc.setPadding(2, 2, 2, 2);
    desc.setWidth(200);
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(desc);

    TextView amount = new TextView(this);
    amount.setText(Utils.CURRENCY_FORMAT.format(trans.getAmount()));
    amount.setPadding(2, 2, 2, 2);
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(amount);

    TextView balance = new TextView(this);
    balance.setText(Utils.CURRENCY_FORMAT.format(trans.getBalance()));
    balance.setPadding(2, 2, 2, 2);
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(balance);

    row.addView(date);
    row.addView(desc);
    row.addView(amount);
    row.addView(balance);

    return row;
  }

  protected boolean showImportMenuItem() {
    return false;
  }

}