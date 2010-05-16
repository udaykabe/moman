package net.deuce.moman.om;

import javax.persistence.*;

@Entity
@Table(name = "financial_institution",  uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class FinancialInstitution extends AbstractEntity<FinancialInstitution> {

  private static final long serialVersionUID = 1L;

  private String name;

  private String url;

  private String financialInstitutionId;

  private String organization;

  public FinancialInstitution() {
    super();
  }

  @Basic
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Basic
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Basic
  public String getFinancialInstitutionId() {
    return financialInstitutionId;
  }

  public void setFinancialInstitutionId(String financialInstitutionId) {
    this.financialInstitutionId = financialInstitutionId;
  }

  @Basic
  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }


  public int compareTo(FinancialInstitution o) {
    return compare(this, o);
  }


  public int compare(FinancialInstitution o1, FinancialInstitution o2) {
    return compareObjects(o1.name, o2.name);
  }

  @Id
  @GeneratedValue
  public Long getId() {
    return super.getId();
  }

  @Basic
  public String getUuid() {
    return super.getUuid();
  }
}
