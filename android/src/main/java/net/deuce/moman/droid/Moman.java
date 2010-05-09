package net.deuce.moman.droid;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import net.deuce.moman.client.HttpRequest;
import net.deuce.moman.client.HttpRequestUtils;
import net.deuce.moman.client.model.EnvelopeClient;
import net.deuce.moman.util.Utils;
import org.dom4j.Document;
import org.dom4j.Element;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Moman extends BaseActivity {

  protected static final int MENU_QUIT = 0;
  protected static final int MENU_ACCOUNTS = 1;
  protected static final int MENU_TRANSACTIONS = 2;
  protected static final int MENU_TRANSFER = 3;

  protected static final String TARGET_ENVELOPE = "TARGET_ENVELOPE";
  protected static final String CURRENT_ENVELOPE = "CURRENT_ENVELOPE";
  protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  protected static Stack<EnvelopeClient> currentEnvelope;
  protected static EnvelopeClient targetEnvelope;
  protected static EnvelopeClient selectedEnvelope;

  private LinearLayout ll;

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
  }

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) {

//    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    // test the server

//    if (true) throw new RuntimeException("Weeeee");

    if (currentEnvelope == null) {
      currentEnvelope = new Stack<EnvelopeClient>();
      currentEnvelope.push(getRootEnvelope());
    }

    ScrollView sv = new ScrollView(this);
    ll = new LinearLayout(this);
    ll.setOrientation(LinearLayout.VERTICAL);
    sv.addView(ll);

    displayEnvelopes();

    setContentView(sv);
  }

  private void displayEnvelopes() {

    ll.removeAllViews();

    TextView title = new TextView(this);
    title.setText(getEnvelopeLabel(currentEnvelope.peek()));
    title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    title.setTypeface(Typeface.DEFAULT_BOLD);
    title.setTextSize(title.getTextSize() + 2);
    ll.addView(title);

    TableLayout table = new TableLayout(this);
    LayoutUtils.Layout.WidthFill_HeightFill.applyViewGroupParams(table);

    AnimUtils.setLayoutAnim_slideupfrombottom(table, this);

    ll.addView(table);

    for (final EnvelopeClient client : getChildren(currentEnvelope.peek())) {
      TableRow row = createRow(client);
      LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableLayoutParams(row);
      row.setPadding(2, 2, 2, 2);
      table.addView(row);
    }

  }

  public TableRow createRow(final EnvelopeClient env) {
    TableRow row = new TableRow(this);

    Button envelopeButton = new Button(this);
    envelopeButton.setText(getEnvelopeLabel(env));
    envelopeButton.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    envelopeButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        currentEnvelope.push(env);
        displayEnvelopes();
      }
    });
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(envelopeButton);

    Button editButton = new Button(this);
    editButton.setText("E");
    editButton.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    editButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        targetEnvelope = env;
        Intent intent = new Intent(Moman.this, EnvelopeEdit.class);
        startActivity(intent);
      }
    });
    editButton.setEnabled(env.isEditable());
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(editButton);

    Button moveButton = new Button(this);
    moveButton.setText("M");
    moveButton.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    moveButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        targetEnvelope = env;
        Intent intent = new Intent(Moman.this, EnvelopeSelect.class);
        startActivity(intent);
      }
    });
    moveButton.setEnabled(env.isEditable());
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(moveButton);

    Button deleteButton = new Button(this);
    deleteButton.setText("X");
    deleteButton.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    deleteButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        targetEnvelope = env;
        Intent intent = new Intent(Moman.this, EnvelopeDelete.class);
        startActivity(intent);
      }
    });
    deleteButton.setEnabled(env.isEditable());
    LayoutUtils.Layout.WidthWrap_HeightWrap.applyTableRowParams(deleteButton);

    row.addView(envelopeButton);
    row.addView(editButton);
    row.addView(moveButton);
    row.addView(deleteButton);

    return row;
  }

  @Override
  protected void onRestart() {
    super.onRestart();

    if (selectedEnvelope != null) {
      moveEnvelope(selectedEnvelope, targetEnvelope);
    }

    if (targetEnvelope != null) {
      targetEnvelope = null;
      displayEnvelopes();
    }

  }

  public void onBackPressed() {
    if (currentEnvelope.size() > 1) {
      currentEnvelope.pop();
      displayEnvelopes();
    } else {
      super.onBackPressed();
    }
  }

  private String getEnvelopeLabel(EnvelopeClient env) {
    return env.getName() + " (" + Utils.CURRENCY_FORMAT.format(env.getBalance()) + ")";
  }

  protected List<EnvelopeClient> getChildren(EnvelopeClient parent) {

    try {
      HttpRequest req = HttpRequest.newGetRequest(buildBaseUrl(new String[]{"envelope", "getEntityProperty", parent.getUuid(), "children"}));

      Document doc = HttpRequestUtils.executeRequest(req.buildMethod(), true, false);

      List<EnvelopeClient> list = new LinkedList<EnvelopeClient>();

      List<Element> entities = doc.selectNodes("//envelope");
      if (entities == null || entities.size() == 0) return list;

      EnvelopeClient client;
      for (Element entity : entities) {
        client = new EnvelopeClient();
        client.buildEntityClient(client, entity);
        list.add(client);
      }

      return list;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected void moveEnvelope(EnvelopeClient env, EnvelopeClient child) {

    try {
      HttpRequest req = HttpRequest.newGetRequest(buildBaseUrl(new String[]{"envelope", "executeCommand", "addChildCommand",
        env.getUuid(), child.getUuid()}));
      Document doc = HttpRequestUtils.executeRequest(req.buildMethod(), true, true);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected EnvelopeClient getRootEnvelope() {

    try {
      HttpRequest req = HttpRequest.newGetRequest(buildBaseUrl(new String[]{"envelope", "executeCommand", "getRootEnvelopeCommand"}));

      Document doc = HttpRequestUtils.executeRequest(req.buildMethod(), true, false);

      Element rootEnvelope = (Element) doc.selectSingleNode("//envelope");
      if (rootEnvelope == null) {
        throw new RuntimeException("No root envelope exists.");
      }

      EnvelopeClient client = new EnvelopeClient();
      client.buildEntityClient(client, rootEnvelope);

      return client;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
