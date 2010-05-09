package net.deuce.moman.droid;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import net.deuce.moman.client.HttpRequest;
import net.deuce.moman.client.HttpRequestUtils;
import net.deuce.moman.client.model.AccountClient;
import net.deuce.moman.client.model.EntityClient;
import net.deuce.moman.client.model.EnvelopeClient;

import java.net.URLEncoder;

public class EnvelopeDelete extends BaseActivity {

  private static final int MENU_ENVELOPES = 1;
  private static final int MENU_QUIT = 2;

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) {

//    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    LinearLayout ll = new LinearLayout(this);
    ll.setOrientation(LinearLayout.VERTICAL);

    TextView question = new TextView(this);
    question.setText("Delete envelope '" + Moman.targetEnvelope.getName() + "'?");
    ll.addView(question);

    Button ok = new Button(this);
    ok.setText("OK");
    ok.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        deleteEnvelope(Moman.targetEnvelope);
        EnvelopeDelete.super.finish();
      }
    });
    ll.addView(ok);

    setContentView(ll);
  }

  protected void deleteEnvelope(EnvelopeClient env) {
    try {
      HttpRequest req = HttpRequest.newGetRequest(buildBaseUrl(new String[]{"envelope", "executeCommand", "removeEnvelopeCommand", env.getUuid()}));

      HttpRequestUtils.executeRequest(req.buildMethod(), true, true);

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