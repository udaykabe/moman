package net.deuce.moman.droid;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.deuce.moman.client.service.EnvelopeClientService;
import net.deuce.moman.client.service.NoAvailableServerException;

public class EnvelopeDelete extends BaseActivity {

  private EnvelopeClientService clientService = EnvelopeClientService.instance();

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) throws NoAvailableServerException {

//    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    LinearLayout ll = new LinearLayout(this);
    ll.setOrientation(LinearLayout.VERTICAL);

    TextView question = new TextView(this);
    question.setText("Delete envelope '" + clientService.getTargetEnvelope().getName() + "'?");
    ll.addView(question);

    Button ok = new Button(this);
    ok.setText("OK");
    ok.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        try {
          clientService.deleteEnvelope(clientService.getTargetEnvelope());
        } catch (NoAvailableServerException e) {
          throw new RuntimeException(e);
        }
        EnvelopeDelete.super.finish();
      }
    });
    ll.addView(ok);

    setContentView(ll);
  }

}