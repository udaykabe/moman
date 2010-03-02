package net.deuce.moman.fi.model;

import net.deuce.moman.model.AbstractEntity;
import net.deuce.moman.model.EntityProperty;

import org.dom4j.Document;

public class FinancialInstitution extends AbstractEntity<FinancialInstitution> {

	private static final long serialVersionUID = 1L;

    public enum Properties implements EntityProperty {
        name(String.class), url(String.class),
        financialInstitutionId(String.class), organization(String.class);
        
		private Class<?> type;
		
		public Class<?> type() { return type; }
		
		private Properties(Class<?> type) { this.type = type; }
    }

	private String name;
	private String url;
	private String financialInstitutionId;
	private String organization;
	
	public FinancialInstitution() {
		super();
	}

	@Override
	public Document toXml() {
		return buildXml(Properties.values());
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (propertyChanged(this.name, name)) {
			this.name = name;
			getMonitor().fireEntityChanged(this);
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if (propertyChanged(this.url, url)) {
			this.url = url;
			getMonitor().fireEntityChanged(this);
		}
	}

	public String getFinancialInstitutionId() {
		return financialInstitutionId;
	}

	public void setFinancialInstitutionId(String financialInstitutionId) {
		if (propertyChanged(this.financialInstitutionId, financialInstitutionId)) {
			this.financialInstitutionId = financialInstitutionId;
			getMonitor().fireEntityChanged(this);
		}
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		if (propertyChanged(this.organization, organization)) {
			this.organization = organization;
			getMonitor().fireEntityChanged(this);
		}
	}
	
	@Override
	public int compareTo(FinancialInstitution o) {
		return compare(this, o);
	}

	@Override
	public int compare(FinancialInstitution o1, FinancialInstitution o2) {
		return o1.name.compareTo(o2.name);
	}
}
