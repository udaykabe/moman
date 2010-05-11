package net.deuce.moman.droid;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import net.deuce.moman.client.model.AccountClient;
import net.deuce.moman.client.model.EnvelopeClient;
import net.deuce.moman.client.model.TransactionClient;
import net.deuce.moman.client.service.AccountClientService;
import net.deuce.moman.client.service.EnvelopeClientService;
import net.deuce.moman.client.service.FinancialInstitutionClientService;
import net.deuce.moman.client.service.NoAvailableServerException;
import net.deuce.moman.util.Utils;

import java.util.LinkedList;
import java.util.List;

public class EnvelopeTransfer extends BaseActivity {

  private EnvelopeClientService clientService = EnvelopeClientService.instance();
  private AccountClientService accountClientService = AccountClientService.instance();

  private AccountClient targetAccount;
  private EnvelopeClient sourceEnvelope;
  private EnvelopeClient targetEnvelope;

  private Button ok;
  private Button source;
  private Button target;
  private Button account;
  private EditText amount;

  private boolean selectingSource;

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) throws NoAvailableServerException {

    targetAccount = accountClientService.list(null).get(0);
    sourceEnvelope = clientService.peekCurrent();
    targetEnvelope = clientService.peekCurrent();

    LinearLayout ll = new LinearLayout(this);
    ll.setOrientation(LinearLayout.VERTICAL);

    LinearLayout accountLayout = new LinearLayout(this);
    accountLayout.setOrientation(LinearLayout.HORIZONTAL);
    accountLayout.setPadding(5, 5, 5, 5);
    ll.addView(accountLayout);

    TextView accountLabel = new TextView(this);
    accountLabel.setText("Account");
    accountLabel.setWidth(60);
    accountLayout.addView(accountLabel);

    account = new Button(this);
    account.setText(targetAccount.getNickname());
    account.setWidth(200);
    account.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    account.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Intent intent = new Intent(EnvelopeTransfer.this, AccountSelect.class);
        startActivity(intent);
      }
    });
    accountLayout.addView(account);

    LinearLayout sourceLayout = new LinearLayout(this);
    sourceLayout.setOrientation(LinearLayout.HORIZONTAL);
    sourceLayout.setPadding(5, 5, 5, 5);
    ll.addView(sourceLayout);

    TextView sourceLabel = new TextView(this);
    sourceLabel.setText("Source");
    sourceLabel.setWidth(60);
    sourceLayout.addView(sourceLabel);

    source = new Button(this);
    source.setText(getEnvelopeLabel(sourceEnvelope));
    source.setWidth(200);
    source.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    source.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        selectingSource = true;
        Intent intent = new Intent(EnvelopeTransfer.this, EnvelopeSelect.class);
        startActivity(intent);
      }
    });
    sourceLayout.addView(source);

    LinearLayout targetLayout = new LinearLayout(this);
    targetLayout.setOrientation(LinearLayout.HORIZONTAL);
    targetLayout.setPadding(5, 5, 5, 5);
    ll.addView(targetLayout);

    TextView targetLabel = new TextView(this);
    targetLabel.setText("Target");
    targetLabel.setWidth(60);
    targetLayout.addView(targetLabel);

    target = new Button(this);
    target.setText(getEnvelopeLabel(targetEnvelope));
    target.setWidth(200);
    target.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    target.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        selectingSource = false;
        Intent intent = new Intent(EnvelopeTransfer.this, EnvelopeSelect.class);
        startActivity(intent);
      }
    });
    targetLayout.addView(target);

    LinearLayout amountLayout = new LinearLayout(this);
    amountLayout.setOrientation(LinearLayout.HORIZONTAL);
    amountLayout.setPadding(5, 5, 5, 5);
    ll.addView(amountLayout);

    TextView amountLabel = new TextView(this);
    amountLabel.setText("Amount");
    amountLabel.setWidth(60);
    amountLayout.addView(amountLabel);

    amount = new EditText(this);
    amount.setText("$0.00");
    amount.setWidth(200);
    amount.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    amount.addTextChangedListener(new TextWatcher() {
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      public void afterTextChanged(Editable editable) {
        if (Utils.validateCurrency(amount.getText().toString())) {
          amount.setBackgroundColor(Color.WHITE);
          ok.setEnabled(true);
        } else {
          amount.setBackgroundColor(Color.RED);
          ok.setEnabled(false);
        }
      }
    });
    amountLayout.addView(amount);

    ok = new Button(this);
    ok.setText("Ok");
    ok.setPadding(5, 5, 5, 5);
    ok.setWidth(60);
    ok.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        clientService.transferEnvelope(targetAccount, sourceEnvelope, targetEnvelope, Utils.parseCurrency(amount.getText().toString()));
        EnvelopeTransfer.this.finish();
      }
    });
    ll.addView(ok);

    setContentView(ll);
  }

  @Override
  protected void onRestart() {
    super.onRestart();

    if (clientService.getSelectedEnvelope() != null) {
      if (selectingSource) {
        sourceEnvelope = clientService.getSelectedEnvelope();
        source.setText(getEnvelopeLabel(sourceEnvelope));
      } else {
        targetEnvelope = clientService.getSelectedEnvelope();
        target.setText(getEnvelopeLabel(targetEnvelope));
      }
      clientService.setSelectedEnvelope(null);

      if (targetEnvelope.getBalance() < 0) {
        if (sourceEnvelope.getBalance() > 0 && sourceEnvelope.getBalance() >= -targetEnvelope.getBalance()) {
          amount.setText(Utils.formatDouble(sourceEnvelope.getBalance()));
        }
      }
    }

    if (accountClientService.getSelectedAccount() != null) {
      targetAccount = accountClientService.getSelectedAccount();
      account.setText(targetAccount.getNickname());
      accountClientService.setSelectedAccount(null);
    }
  }

  protected boolean showTransferMenuItem() {
    return false;
  }

}