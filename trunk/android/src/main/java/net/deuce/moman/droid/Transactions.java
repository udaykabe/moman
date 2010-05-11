package net.deuce.moman.droid;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.*;
import android.view.accessibility.AccessibilityEvent;
import android.widget.*;
import net.deuce.moman.client.model.TransactionClient;
import net.deuce.moman.client.service.EnvelopeClientService;
import net.deuce.moman.client.service.NoAvailableServerException;
import net.deuce.moman.client.service.TransactionClientService;
import net.deuce.moman.client.service.TransactionListResult;
import net.deuce.moman.util.Utils;

import java.util.List;

public class Transactions extends BaseActivity {

  private static final int TRANSACTION_LIST_PAGE_SIZE = 9;

  private int transactionListPosition = 0;
  private int transactionListTotalSize = -1;
//  private int transactionListPageSize = -1;

  private LinearLayout ll;
  private ScrollView sv;

  private TransactionClientService clientService = TransactionClientService.instance();
  private EnvelopeClientService envelopeClientService = EnvelopeClientService.instance();

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) throws NoAvailableServerException {

    sv = new ScrollView(this);
    ll = new LinearLayout(this);
    ll.setOrientation(LinearLayout.VERTICAL);
    ll.setPadding(5, 5, 5, 5);
    sv.addView(ll);

    setContentView(sv);

    displayTransactions();

  }

  private void displayTransactions() throws NoAvailableServerException {
    ll.removeAllViews();

    sv.scrollTo(0, 0);

    AnimUtils.setLayoutAnim_slideupfrombottom(ll, this);

    TransactionListResult result = clientService.getTransactions(envelopeClientService.peekCurrent(),
        transactionListPosition, TRANSACTION_LIST_PAGE_SIZE);

    transactionListTotalSize = result.getTotalSize();
//    transactionListPageSize = result.getPageSize();

    int rowCount = 0;
    for (TransactionClient trans : result.getTransactions()) {
      createRow(ll, trans, rowCount++);
    }

    if (transactionListPosition + TRANSACTION_LIST_PAGE_SIZE < transactionListTotalSize) {
      Button b = new Button(this);
      b.setText("Fetch more transactions...");
      b.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
      b.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
          transactionListPosition += TRANSACTION_LIST_PAGE_SIZE;
          try {
            displayTransactions();
          } catch (NoAvailableServerException e) {
            throw new RuntimeException(e);
          }
        }
      });
      ll.addView(b);
    }
  }

  public void createRow(LinearLayout ll, TransactionClient trans, int rowNumber) {

    LinearLayout row = new LinearLayout(this);
    row.setOrientation(LinearLayout.HORIZONTAL);
    ll.addView(row);
    if (rowNumber % 2 == 1) {
      row.setBackgroundColor(Color.rgb(0x2f, 0x4f, 0x4f));
    }

    int orientation = getResources().getConfiguration().orientation;

    int firstColWidth = orientation == Configuration.ORIENTATION_LANDSCAPE ? 330 : 170;

    LinearLayout left = new LinearLayout(this);
    left.setOrientation(LinearLayout.VERTICAL);
    row.addView(left);

    TextView desc = new TextView(this);
    desc.setText(trans.getDescription());
    desc.setPadding(2, 2, 2, 2);
    desc.setTextSize(14);
    desc.setWidth(firstColWidth);
    desc.setTypeface(Typeface.DEFAULT_BOLD);
    desc.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    left.addView(desc);

    LinearLayout dateMemo = new LinearLayout(this);
    dateMemo.setOrientation(LinearLayout.HORIZONTAL);
    left.addView(dateMemo);

    TextView date = new TextView(this);
    date.setText(DATE_FORMAT.format(trans.getDate()));
    date.setPadding(2, 2, 2, 2);
    date.setTextSize(10);
    date.setWidth(60);
    date.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    dateMemo.addView(date);

    TextView memo = new TextView(this);
    memo.setText(trans.getMemo() != null ? trans.getMemo() : "");
    memo.setPadding(2, 2, 2, 2);
    memo.setTextSize(10);
    memo.setWidth(firstColWidth - 60);
    memo.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    dateMemo.addView(memo);

    LinearLayout amountCheck = new LinearLayout(this);
    amountCheck.setOrientation(LinearLayout.VERTICAL);
    row.addView(amountCheck);

    TextView amount = new TextView(this);
    amount.setText(Utils.CURRENCY_FORMAT.format(trans.getAmount()));
    amount.setPadding(2, 2, 2, 2);
    amount.setTextSize(10);
    amount.setWidth(75);
    amount.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    amountCheck.addView(amount);

    TextView check = new TextView(this);
    check.setText(trans.getCheckNo() != null ? trans.getCheckNo() : "");
    check.setPadding(2, 2, 2, 2);
    check.setTextSize(10);
    check.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    amountCheck.addView(check);

    TextView balance = new TextView(this);
    balance.setText(Utils.CURRENCY_FORMAT.format(trans.getBalance()));
    balance.setPadding(2, 2, 2, 2);
    balance.setTextSize(10);
    balance.setWidth(75);
    balance.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    row.addView(balance);
  }

  public void onBackPressed() {
    if (transactionListPosition >= TRANSACTION_LIST_PAGE_SIZE) {
      transactionListPosition -= TRANSACTION_LIST_PAGE_SIZE;
      try {
        displayTransactions();
      } catch (NoAvailableServerException e) {
        throw new RuntimeException(e);
      }
      return;
    }
    super.onBackPressed();
  }

  protected boolean showTransactionsMenuItem() {
    return false;
  }

}