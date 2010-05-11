package net.deuce.moman.client.model;

import net.deuce.moman.client.model.EntityClient;


public class FinancialInstitutionClient extends EntityClient {

  private FinancialInstitutionClient financialInstitution = new FinancialInstitutionClient();

  public String getServiceName() {
    return "fi";
  }

  public String getEntityName() {
    return "financialInstitution";
  }

  public String getFinancialInstitutionId() {
    return getProperty("financialInstitutionId");
  }

  public String getId() {
    return getProperty("id");
  }

  public String getName() {
    return getProperty("name");
  }

  public String getOrganization() {
    return getProperty("organization");
  }

  public String getUrl() {
    return getProperty("url");
  }

  public String getUuid() {
    return getProperty("uuid");
  }

  public void setUuid(String uuid) {
    setProperty("uuid", uuid);
  }

  public void setFinancialInstitutionId(String financialInstitutionId) {
    setProperty("financialInstitutionId", financialInstitutionId);
  }

  public void setName(String name) {
    setProperty("name", name);
  }

  public void setOrganization(String organization) {
    setProperty("organization", organization);
  }

  public void setUrl(String url) {
    setProperty("url", url);
  }
}
