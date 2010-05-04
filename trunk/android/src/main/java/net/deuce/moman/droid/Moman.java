package net.deuce.moman.droid;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import net.deuce.moman.droid.model.EnvelopeClient;
import net.deuce.moman.util.Utils;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

  protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  protected static Stack<EnvelopeClient> currentEnvelope;

  private LinearLayout ll;

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) {

//    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    // test the server

//    if (true) throw new RuntimeException("Weeeee");

    boolean goToServerActivitiy = false;
    try {
      InetAddress address = InetAddress.getByName(SERVER.split(":")[0]);
      goToServerActivitiy = !address.isReachable(5000);
    } catch (Exception e) {
      goToServerActivitiy = true;
    }

    if (goToServerActivitiy) {
      server();
      return;
    }

    ScrollView sv = new ScrollView(this);
    ll = new LinearLayout(this);
    ll.setOrientation(LinearLayout.VERTICAL);
    sv.addView(ll);

    if (currentEnvelope == null) {
      currentEnvelope = new Stack<EnvelopeClient>();
      currentEnvelope.push(getRootEnvelope());
    }

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

    for (final EnvelopeClient client : getChildren(currentEnvelope.peek())) {
      Button b = new Button(this);
      b.setText(getEnvelopeLabel(client));
      b.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
      b.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
          currentEnvelope.push(client);
//          Intent intent = new Intent(Moman.this, Moman.class);
//          startActivity(intent);
          displayEnvelopes();
        }
      });
      ll.addView(b);
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
      HttpRequest req = HttpRequest.newGetRequest(buildBaseUrl() + "envelope");
      req.addParameter("action", "14");
      req.addParameter("uuid", parent.getUuid());
      req.addParameter("property", "children");

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

  protected EnvelopeClient getRootEnvelope() {

    try {
      HttpRequest req = HttpRequest.newGetRequest(buildBaseUrl() + "envelope");
      req.addParameter("action", "7");
      req.addParameter("command", "getRootEnvelopeCommand");

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
