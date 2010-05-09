package net.deuce.moman.droid;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import net.deuce.moman.client.HttpRequest;
import net.deuce.moman.client.HttpRequestUtils;
import net.deuce.moman.client.model.AccountClient;
import net.deuce.moman.client.model.EntityClient;
import net.deuce.moman.client.model.EnvelopeClient;
import net.deuce.moman.util.Utils;
import org.dom4j.Document;
import org.dom4j.Element;

import java.net.URLEncoder;

public class EnvelopeEdit extends BaseActivity {

  private static final int MENU_ENVELOPES = 1;
  private static final int MENU_QUIT = 2;

  private Button ok;
  private String name;
  private Boolean enabled;
  private Double budget;

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) {

    TableLayout table = new TableLayout(this);
    LayoutUtils.Layout.WidthFill_HeightFill.applyViewGroupParams(table);

    AnimUtils.setLayoutAnim_slideupfrombottom(table, this);

    EnvelopeClient env = Moman.targetEnvelope;

    enabled = env.isEnabled();
    name = env.getName();
    budget = env.getBudget();

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
        name = value.getText().toString();
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
        enabled = value.isChecked();
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
          budget = Utils.parseCurrency(value.getText().toString());
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
    persist(Moman.targetEnvelope);
    finish(); 
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(0, MENU_QUIT, 0, "Quit");
    return true;
  }
  
  protected void persist(EnvelopeClient env) {
    try {

      if (!env.getName().equals(name) || env.isEnabled() != enabled.booleanValue() || env.getBudget() != budget.doubleValue()) {
        HttpRequest req = HttpRequest.newGetRequest(buildBaseUrl(new String[]{
            "envelope", "edit", env.getUuid(),
            "name", URLEncoder.encode(name, "UTF-8"),
            "enabled", Boolean.toString(enabled),
            "budget", Double.toString(budget)
        }));

        HttpRequestUtils.executeRequest(req.buildMethod(), true, false);
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}