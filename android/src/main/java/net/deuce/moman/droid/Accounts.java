package net.deuce.moman.droid;

import android.os.Bundle;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import net.deuce.moman.client.model.AccountClient;
import net.deuce.moman.client.service.AccountClientService;
import net.deuce.moman.client.service.NoAvailableServerException;

public class Accounts extends BaseActivity {

  private static final int MENU_ENVELOPES = 1;
  private static final int MENU_QUIT = 2;

  private AccountClientService clientService = AccountClientService.instance();

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) throws NoAvailableServerException {

//    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    ScrollView sv = new ScrollView(this);
    LinearLayout ll = new LinearLayout(this);
    ll.setOrientation(LinearLayout.VERTICAL);
    sv.addView(ll);

    for (final AccountClient client : clientService.list(null)) {
      final CheckBox b = new CheckBox(this);
      b.setText(client.getNickname());
      b.setChecked(client.isSelected());
      b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
          client.setSelected(b);
          try {
            clientService.persist(client);
          } catch (NoAvailableServerException e) {
            throw new RuntimeException(e);
          }
        }
      });
      ll.addView(b);
    }

    setContentView(sv);
  }

  protected boolean showAccountsMenuItem() {
    return false;
  }

}