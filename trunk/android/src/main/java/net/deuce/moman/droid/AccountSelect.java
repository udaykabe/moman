package net.deuce.moman.droid;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import net.deuce.moman.client.model.AccountClient;
import net.deuce.moman.client.model.EnvelopeClient;
import net.deuce.moman.client.service.AccountClientService;
import net.deuce.moman.client.service.EnvelopeClientService;
import net.deuce.moman.client.service.NoAvailableServerException;

import java.util.Comparator;
import java.util.List;

public class AccountSelect extends BaseActivity {

  private LinearLayout ll;

  private AccountClientService clientService = AccountClientService.instance();

  /**
   * Called when the activity is first created.
   */
  @Override
  protected void doOnCreate(Bundle savedInstanceState) throws NoAvailableServerException {

    ScrollView sv = new ScrollView(this);
    ll = new LinearLayout(this);
    ll.setOrientation(LinearLayout.VERTICAL);
    sv.addView(ll);

    displayAccounts();

    setContentView(sv);
  }

  private void displayAccounts() throws NoAvailableServerException {
    ll.removeAllViews();

    for (final AccountClient account : fetchAccounts()) {
      Button b = new Button(this);
      b.setText(account.getNickname());
      b.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
      b.setWidth(100);
      b.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
          clientService.setSelectedAccount(account);
          AccountSelect.this.finish();
        }
      });
      ll.addView(b);
    }
  }

  private List<AccountClient> fetchAccounts() throws NoAvailableServerException {
    return clientService.list(new Comparator<AccountClient>() {
      public int compare(AccountClient o1, AccountClient o2) {
        return o1.getNickname().compareTo(o2.getNickname());
      }
    });
  }

}