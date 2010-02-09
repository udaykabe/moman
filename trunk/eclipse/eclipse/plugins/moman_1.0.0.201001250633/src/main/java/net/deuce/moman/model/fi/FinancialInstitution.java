package net.deuce.moman.model.fi;

import net.deuce.moman.model.EntityMonitor;
import net.deuce.moman.model.MomanEntity;

public class FinancialInstitution extends MomanEntity {

	private static final long serialVersionUID = 1L;

	private String name;
	private String url;
	private String financialInstitutionId;
	private String organization;
	private transient EntityMonitor<FinancialInstitution> monitor = new EntityMonitor<FinancialInstitution>();

	
	public void setMonitor(EntityMonitor<FinancialInstitution> monitor) {
		this.monitor = monitor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (propertyChanged(this.name, name)) {
			this.name = name;
			monitor.fireEntityChanged(this);
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if (propertyChanged(this.url, url)) {
			this.url = url;
			monitor.fireEntityChanged(this);
		}
	}

	public String getFinancialInstitutionId() {
		return financialInstitutionId;
	}

	public void setFinancialInstitutionId(String financialInstitutionId) {
		if (propertyChanged(this.financialInstitutionId, financialInstitutionId)) {
			this.financialInstitutionId = financialInstitutionId;
			monitor.fireEntityChanged(this);
		}
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		if (propertyChanged(this.organization, organization)) {
			this.organization = organization;
			monitor.fireEntityChanged(this);
		}
	}
}
