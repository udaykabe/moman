package net.deuce.moman.client.model;

import net.deuce.moman.client.model.EntityClient;
import net.deuce.moman.client.model.FinancialInstitutionClient;

import java.util.Date;

public class AccountClient extends EntityClient {

  private FinancialInstitutionClient financialInstitution;

  public String getEntityName() {
    return "account";
  }

  public String getServiceName() {
    return "account";
  }

  public String getAccountId() {
    return getProperty("accountId");
  }

  public double getBalance() {
    return getDouble("balance");
  }

  public String getBankId() {
    return getProperty("bankId");
  }

  public String getFinancialInstitutionId() {
    return getProperty("financialInstitutionId");
  }

  public void setFinancialInstitutionId(String uuid) {
    setProperty("financialInstitutionId", uuid);
  }

  public FinancialInstitutionClient getFinancialInstitutionClient() {
    if (financialInstitution == null) {
      financialInstitution = (FinancialInstitutionClient) fetchEntity(FinancialInstitutionClient.class, getFinancialInstitutionId());
    }
    return financialInstitution;
  }

  public double getInitialBalance() {
    return getDouble("initialBalance");
  }

  public Long getId() {
    return getLong("id");
  }

  public Date getLastDownloadDate() {
    return getDate("lastDownloadDate");
  }

  public Date getLastReconciledDate() {
    return getDate("lastReconciledDate");
  }

  public double getLastReconciledEndingBalance() {
    return getDouble("lastReconciledEndingBalance");
  }

  public String getNickname() {
    return getProperty("nickname");
  }

  public double getOnlineBalance() {
    return getDouble("onlineBalance");
  }

  public String getPassword() {
    return getProperty("password");
  }

  public boolean isSelected() {
    return getBoolean("selected");
  }

  public String getStatus() {
    return getProperty("status");
  }

  public String getUsername() {
    return getProperty("username");
  }

  public String getUuid() {
    return getProperty("uuid");
  }

  public boolean isSupportsDownloading() {
    return getBoolean("supportsDownloading");
  }

  public void setAccountId(String accountId) {
    setProperty("accountId", accountId);
  }

  public void setBalance(double balance) {
    setDouble("balance", balance);
  }

  public void setBankId(String bankId) {
    setProperty("bankId", bankId);
  }

  public void setInitialBalance(double initialBalance) {
    setDouble("initialBalance", initialBalance);
  }

  public void setLastDownloadDate(Date lastDownloadDate) {
    setDate("lastDownloadDate", lastDownloadDate);
  }

  public void setLastReconciledDate(Date lastReconciledDate) {
    setDate("lastReconciledDate", lastReconciledDate);
  }

  public void setLastReconciledEndingBalance(double lastReconciledEndingBalance) {
    setDouble("lastReconciledEndingBalance", lastReconciledEndingBalance);
  }

  public void setNickname(String nickname) {
    setProperty("nickname", nickname);
  }

  public void setOnlineBalance(double onlineBalance) {
    setDouble("onlineBalance", onlineBalance);
  }

  public void setPassword(String password) {
    setProperty("password", password);
  }

  public void setSelected(boolean selected) {
    setBoolean("selected", selected);
  }

  public void setStatus(String status) {
    setProperty("status", status);
  }

  public void setSupportsDownloading(boolean supportsDownloading) {
    setBoolean("supportsDownloading", supportsDownloading);
  }

  public void setUsername(String username) {
    setProperty("username", username);
  }

  public void setUuid(String uuid) {
    setProperty("uuid", uuid);
  }

}
