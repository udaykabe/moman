package net.deuce.moman.droid;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import net.deuce.moman.client.HttpRequest;
import net.deuce.moman.client.HttpRequestUtils;
import net.deuce.moman.client.model.EnvelopeClient;
import net.deuce.moman.client.model.TransactionClient;
import net.deuce.moman.util.Utils;
import org.dom4j.Document;
import org.dom4j.Element;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class EnvelopeSelect extends BaseActivity {

  private static final int MENU_ENVELOPES = 1;
  private static final int MENU_QUIT = 2;

  private LinearLayout ll;

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) {

    ScrollView sv = new ScrollView(this);
    ll = new LinearLayout(this);
    ll.setOrientation(LinearLayout.VERTICAL);
    sv.addView(ll);

    displayEnvelopes();

    setContentView(sv);
  }

  private void displayEnvelopes() {
    ll.removeAllViews();

    for (final EnvelopeClient env : fetchEnvelopes()) {
      Button b = new Button(this);
      b.setText(env.getName());
      b.setWidth(100);
      b.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
          Moman.selectedEnvelope = env;
          EnvelopeSelect.this.finish();
        }
      });
      ll.addView(b);
    }
  }

  private List<EnvelopeClient> fetchEnvelopes() {
    try {
      HttpRequest req = HttpRequest.newGetRequest(buildBaseUrl(
          new String[]{"envelope", "list"}));

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

      Collections.sort(list, new Comparator<EnvelopeClient>() {
        public int compare(EnvelopeClient o1, EnvelopeClient o2) {
          return o1.getName().compareTo(o2.getName());
        }
      });

      return list;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(0, MENU_QUIT, 0, "Quit");
    return true;
  }

}