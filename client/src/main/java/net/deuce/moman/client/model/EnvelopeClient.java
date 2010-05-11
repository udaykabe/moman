package net.deuce.moman.client.model;

import net.deuce.moman.client.model.EntityClient;

import java.util.Date;

public class EnvelopeClient extends EntityClient {

  public String getEntityName() {
    return "envelope";
  }

  public String getServiceName() {
    return "envelope";
  }

  public boolean isEditable() {
    return getBoolean("editable");
  }

  public boolean isAvailable() {
    return getBoolean("available");
  }

  public boolean isEnabled() {
    return getBoolean("enabled");
  }

  public boolean isExpanded() {
    return getBoolean("expanded");
  }

  public boolean isMonthly() {
    return getBoolean("monthly");
  }

  public boolean isRoot() {
    return getBoolean("root");
  }

  public boolean isSavingsGoals() {
    return getBoolean("savingsGoals");
  }

  public boolean isUnassigned() {
    return getBoolean("unassigned");
  }

  public double getBudget() {
    return getDouble("budget");
  }

  public int getDueDay() {
    return getInt("dueDay");
  }

  public String getFrequency() {
    return getProperty("frequency");
  }

  public double getBalance() {
    return getDouble("balance");
  }

  public int getIndex() {
    return getInt("index");
  }

  public String getName() {
    return getProperty("name");
  }

  public Date getSavingsGoalDate() {
    return getDate("savingsGoalDate");
  }

  public double getSavingsGoalOverrideAmount() {
    return getDouble("savingsGoalOverrideAmount");
  }

  public long getId() {
    return getLong("id");
  }

  public String getUuid() {
    return getProperty("uuid");
  }

  public String getParentId() {
    return getProperty("parentId");
  }

  public void setEnabled(boolean b) {
    setBoolean("enabled", b);
  }

  public void setExpanded(boolean b) {
    setBoolean("expanded", b);
  }

  public void setBudget(double val) {
    setDouble("budget", val);
  }

  public void setDueDay(int val) {
    setInt("dueDay", val);
  }

  public void setFrequency(String val) {
    setProperty("frequency", val);
  }

  public void setIndex(int val) {
    setInt("index", val);
  }

  public void setName(String val) {
    setProperty("name", val);
  }

  public void setSavingsGoalDate(Date val) {
    setDate("savingsGoalDate", val);
  }

  public void setSavingsGoalOverrideAmount(double val) {
    setDouble("savingsGoalOverrideAmount", val);
  }

}
