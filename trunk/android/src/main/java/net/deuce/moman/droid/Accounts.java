package net.deuce.moman.droid;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import net.deuce.moman.droid.model.AccountClient;
import net.deuce.moman.droid.model.EntityClient;

public class Accounts extends BaseActivity {

  private static final int MENU_ENVELOPES = 1;
  private static final int MENU_QUIT = 2;

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) {

//    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    
    ScrollView sv = new ScrollView(this);
    LinearLayout ll = new LinearLayout(this);
    ll.setOrientation(LinearLayout.VERTICAL);
    sv.addView(ll);

    for (EntityClient client : getEntityList(AccountClient.class)) {
      AccountClient accountClient = (AccountClient) client;
      CheckBox b = new CheckBox(this);
      b.setText(accountClient.getNickname());
      b.setChecked(accountClient.isSelected());
      ll.addView(b);
    }

    setContentView(sv);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(0, MENU_ENVELOPES, 0, "Envelopes");
    menu.add(0, MENU_QUIT, 0, "Quit");
    return true;
  }
}