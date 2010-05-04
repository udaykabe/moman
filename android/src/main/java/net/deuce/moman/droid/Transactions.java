package net.deuce.moman.droid;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import net.deuce.moman.droid.model.EnvelopeClient;
import net.deuce.moman.droid.model.TransactionClient;
import net.deuce.moman.util.Utils;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.LinkedList;
import java.util.List;

public class Transactions extends BaseActivity {

  private static final int TRANSACTION_LIST_PAGE_SIZE = 10;

  private int transactionListPosition = 0;
  private int transactionListTotalSize = -1;
  private int transactionListPageSize = -1;

  private LinearLayout ll;
  private ScrollView sv;

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) {

    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    sv = new ScrollView(this);
    ll = new LinearLayout(this);
    ll.setOrientation(LinearLayout.VERTICAL);
    sv.addView(ll);

    displayTransactions();

    setContentView(sv);
  }

  private void displayTransactions() {
    ll.removeAllViews();

    sv.scrollTo(0, 0);

    TableLayout table = new TableLayout(this);
    LayoutUtils.Layout.WidthFill_HeightFill.applyViewGroupParams(table);

    // set which column is expandable/can grow
//    table.setColumnStretchable(1, true);

    // apply layout animation
    AnimUtils.setLayoutAnim_slideupfrombottom(table, this);

    for (TransactionClient trans : getTransactions()) {
      TableRow row = createRow(trans);
      LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableLayoutParams(row);
      row.setPadding(5, 5, 5, 5);
      table.addView(row);
    }

    ll.addView(table);

    if (transactionListPosition + transactionListPageSize < transactionListTotalSize) {
      Button b = new Button(this);
      b.setText("Fetch more transactions...");
      b.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
      b.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
          transactionListPosition += transactionListPageSize;
          displayTransactions();
        }
      });
      ll.addView(b);
    }
  }

  private List<TransactionClient> getTransactions() {
    EnvelopeClient current = Moman.currentEnvelope.peek();

    try {
      HttpRequest req = HttpRequest.newGetRequest(buildBaseUrl() + "transaction");
      req.addParameter("action", "7");
      req.addParameter("command", String.format("getSelectedAccountTransactionsCommand %1$s, true, false, true, %2$d, %3$d",
          current.getUuid(), transactionListPosition, TRANSACTION_LIST_PAGE_SIZE));

      Document doc = HttpRequestUtils.executeRequest(req.buildMethod(), true, false);

      Element root = (Element) doc.selectSingleNode("//transactions");
      transactionListTotalSize = Integer.valueOf(root.attributeValue("totalSize"));
      transactionListPageSize = Integer.valueOf(root.attributeValue("pageSize"));

      List<TransactionClient> list = new LinkedList<TransactionClient>();

      List<Element> entities = doc.selectNodes("//transaction");
      if (entities == null || entities.size() == 0) return list;

      TransactionClient client;
      for (Element entity : entities) {
        client = new TransactionClient();
        client.buildEntityClient(client, entity);
        list.add(client);
      }

      return list;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public TableRow createRow(TransactionClient trans) {
    TableRow row = new TableRow(this);

    TextView date = new TextView(this);
    date.setText(DATE_FORMAT.format(trans.getDate()));
    date.setPadding(5, 5, 5, 5);
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(date);

    TextView desc = new TextView(this);
    desc.setText(trans.getDescription());
    desc.setPadding(5, 5, 5, 5);
    desc.setWidth(200);
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(desc);

    TextView amount = new TextView(this);
    amount.setText(Utils.CURRENCY_FORMAT.format(trans.getAmount()));
    amount.setPadding(5, 5, 5, 5);
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(amount);

    TextView balance = new TextView(this);
    balance.setText(Utils.CURRENCY_FORMAT.format(trans.getBalance()));
    balance.setPadding(5, 5, 5, 5);
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(balance);

    row.addView(date);
    row.addView(desc);
    row.addView(amount);
    row.addView(balance);

    return row;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(0, MENU_QUIT, 0, "Quit");
    return true;
  }
}