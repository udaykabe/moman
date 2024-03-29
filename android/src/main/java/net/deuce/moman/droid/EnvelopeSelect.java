package net.deuce.moman.droid;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import net.deuce.moman.client.model.EnvelopeClient;
import net.deuce.moman.client.service.EnvelopeClientService;
import net.deuce.moman.client.service.NoAvailableServerException;

import java.util.Comparator;
import java.util.List;

public class EnvelopeSelect extends BaseActivity {

  private LinearLayout ll;

  private EnvelopeClientService clientService = EnvelopeClientService.instance();

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) throws NoAvailableServerException {

    ScrollView sv = new ScrollView(this);
    ll = new LinearLayout(this);
    ll.setOrientation(LinearLayout.VERTICAL);
    sv.addView(ll);

    displayEnvelopes();

    setContentView(sv);
  }

  private void displayEnvelopes() throws NoAvailableServerException {
    ll.removeAllViews();

    for (final EnvelopeClient env : fetchEnvelopes()) {
      Button b = new Button(this);
      b.setText(getEnvelopeLabel(env));
      b.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
      b.setWidth(100);
      b.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
          clientService.setSelectedEnvelope(env);
          EnvelopeSelect.this.finish();
        }
      });
      ll.addView(b);
    }
  }

  private List<EnvelopeClient> fetchEnvelopes() throws NoAvailableServerException {
    return clientService.list(new Comparator<EnvelopeClient>() {
      public int compare(EnvelopeClient o1, EnvelopeClient o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
  }

}