package net.deuce.moman.client.model;

import net.deuce.moman.client.model.EntityClient;

import java.util.Date;

public class TransactionClient extends EntityClient {

  public String getEntityName() {
    return "transaction";
  }

  public String getServiceName() {
    return "transaction";
  }

  public boolean isEnvelopeTransfer() {
    return getProperty("etransferId") != null;
  }

  public double getAmount() {
    return getDouble("amount");
  }

  public String getType() {
    return getProperty("type");
  }

  public Date getDate() {
    return getDate("date");
  }

  public String getDescription() {
    return getProperty("description");
  }

  public String getExternalId() {
    return getProperty("externalId");
  }

  public String getMatchedTransactionId() {
    return getProperty("matchedTransactionId");
  }

  public boolean isMatched() {
    return getMatchedTransactionId() != null;
  }

  public double getBalance() {
    return getDouble("balance");
  }

  public String getMemo() {
    return getProperty("memo");
  }

  public String getCheckNo() {
    return getProperty("checkNo");
  }

  public String getRef() {
    return getProperty("ref");
  }

  public String getStatus() {
    return getProperty("status");
  }

  public long getId() {
    return getLong("id");
  }

  public String getUuid() {
    return getProperty("uuid");
  }

  public void setAmount(double val) {
    setDouble("amount", val);
  }

  public void setType(String val) {
    setProperty("type", val);
  }

  public void setDate(Date val) {
    setDate("date", val);
  }

  public void setDescription(String val) {
    setProperty("description", val);
  }

  public void setExternalId(String val) {
    setProperty("externalId", val);
  }

  public void setBalance(double val) {
    setDouble("balance", val);
  }

  public void setMemo(String val) {
    setProperty("memo", val);
  }

  public void setCheckNo(String val) {
    setProperty("checkNo", val);
  }

  public void setRef(String val) {
    setProperty("ref", val);
  }

  public void setStatus(String val) {
    setProperty("status", val);
  }

}
