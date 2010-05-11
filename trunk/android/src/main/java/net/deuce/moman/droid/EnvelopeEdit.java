package net.deuce.moman.droid;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import net.deuce.moman.client.model.EnvelopeClient;
import net.deuce.moman.client.service.EnvelopeClientService;
import net.deuce.moman.client.service.NoAvailableServerException;
import net.deuce.moman.util.Utils;

public class EnvelopeEdit extends BaseActivity {

  private Button ok;

  private EnvelopeClient editedClient;

  private EnvelopeClientService clientService = EnvelopeClientService.instance();

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) throws NoAvailableServerException {

    TableLayout table = new TableLayout(this);
    LayoutUtils.Layout.WidthFill_HeightFill.applyViewGroupParams(table);

    AnimUtils.setLayoutAnim_slideupfrombottom(table, this);

    EnvelopeClient env = clientService.getTargetEnvelope();

    editedClient = new EnvelopeClient();
    editedClient.clone(env);

    createEnabledRow(table, env);

    createNameRow(table, env);

    createBudgetRow(table, env);

    TableRow row = new TableRow(this);
    ok = new Button(this);
    ok.setText("Ok");
    ok.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        finishUp();
      }
    });
    ok.setGravity(Gravity.CENTER);
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(ok);
    row.setPadding(2, 2, 2, 2);
    row.addView(ok);
    table.addView(row);

    setContentView(table);
  }

  public void createNameRow(TableLayout table, final EnvelopeClient env) {
    TableRow row = new TableRow(this);

    TextView label = new TextView(this);
    label.setText("Name");
    label.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(label);
    row.addView(label);

    final EditText value = new EditText(this);
    value.setMaxLines(1);
    value.setText(env.getName());
    value.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(value);
    row.addView(value);
    value.addTextChangedListener(new TextWatcher() {
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      public void afterTextChanged(Editable editable) {
        editedClient.setName(value.getText().toString());
      }
    });

    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableLayoutParams(row);
    row.setPadding(2, 2, 2, 2);
    table.addView(row);
  }

  public void createEnabledRow(TableLayout table, final EnvelopeClient env) {
    TableRow row = new TableRow(this);

    TextView label = new TextView(this);
    label.setText("Enabled");
    label.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(label);
    row.addView(label);

    final CheckBox value = new CheckBox(this);
    value.setChecked(env.isEnabled());
    value.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        editedClient.setEnabled(value.isChecked());
      }
    });
    row.addView(value);

    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableLayoutParams(row);
    row.setPadding(2, 2, 2, 2);
    table.addView(row);
  }

  public void createBudgetRow(TableLayout table, final EnvelopeClient env) {
    TableRow row = new TableRow(this);

    TextView label = new TextView(this);
    label.setText("Budget");
    label.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(label);
    row.addView(label);

    final EditText value = new EditText(this);
    value.setMaxLines(1);
    value.setText(Utils.formatDouble(env.getBudget()));
    value.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(value);
    value.addTextChangedListener(new TextWatcher() {
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      public void afterTextChanged(Editable editable) {
        if (Utils.validateCurrency(value.getText().toString())) {
          value.setBackgroundColor(Color.WHITE);
          ok.setEnabled(true);
          editedClient.setBudget(Utils.parseCurrency(value.getText().toString()));
        } else {
          value.setBackgroundColor(Color.RED);
          ok.setEnabled(false);
        }
      }
    });
    row.addView(value);

    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableLayoutParams(row);
    row.setPadding(2, 2, 2, 2);
    table.addView(row);
  }

  private void finishUp() {
    try {
      clientService.persist(editedClient, clientService.getTargetEnvelope());
      clientService.getTargetEnvelope().clone(editedClient);
    } catch (NoAvailableServerException e) {
      throw new RuntimeException(e);
    }
    finish();
  }

}