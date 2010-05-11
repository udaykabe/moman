package net.deuce.moman.droid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.deuce.moman.client.service.EntityClientService;

public class Server extends BaseActivity {

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) {

    LinearLayout ll = new LinearLayout(this);
    ll.setOrientation(LinearLayout.VERTICAL);

    TextView label = new TextView(this);
    label.setText("Enter server:");
    label.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    ll.addView(label);

    final EditText server = new EditText(this);
    server.setText(EntityClientService.getServer().split(":")[0]);
    server.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    server.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
    ll.addView(server);

    label = new TextView(this);
    label.setText("Enter port:");
    label.setInputType(InputType.TYPE_CLASS_NUMBER);
    label.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    ll.addView(label);

    final EditText port = new EditText(this);
    port.setText(EntityClientService.getServer().split(":")[1]);
    port.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    port.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
    ll.addView(port);

    Button ok = new Button(this);
    ok.setText("Ok");
    ok.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        EntityClientService.setServer(server.getText().toString() + ":" + port.getText().toString());
        Intent intent = new Intent(Server.this, Moman.class);
        startActivity(intent);
      }
    });
    ll.addView(ok);

    setContentView(ll);

    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    mgr.showSoftInput(server, InputMethodManager.SHOW_IMPLICIT);
  }

}