package net.deuce.moman.client.service;

import net.deuce.moman.client.HttpRequest;
import net.deuce.moman.client.HttpRequestUtils;
import net.deuce.moman.client.model.AccountClient;
import net.deuce.moman.client.model.EnvelopeClient;
import net.deuce.moman.client.service.EntityClientService;
import net.deuce.moman.client.service.NoAvailableServerException;

import java.net.URLEncoder;

public class AccountClientService extends EntityClientService<AccountClient> {

  private static AccountClientService __instance = new AccountClientService();

  public static AccountClientService instance() {
    return __instance;
  }

  private AccountClient selectedAccount = null;

  private AccountClientService() {
  }

  public AccountClient getSelectedAccount() {
    return selectedAccount;
  }

  public void setSelectedAccount(AccountClient selectedAccount) {
    this.selectedAccount = selectedAccount;
  }

  @Override
  protected String getServiceName() {
    return "account";
  }

  @Override
  protected String getEntityName() {
    return "account";
  }

  @Override
  protected AccountClient newEntity() {
    return new AccountClient();
  }

  public void persist(AccountClient account) throws NoAvailableServerException {
    try {

      HttpRequest req = HttpRequest.newGetRequest(buildServiceUrl(new String[]{
          "account", "edit", account.getUuid(),
          "selected", Boolean.toString(account.isSelected())
      }));

      HttpRequestUtils.executeRequest(req.buildMethod(), true, false);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}