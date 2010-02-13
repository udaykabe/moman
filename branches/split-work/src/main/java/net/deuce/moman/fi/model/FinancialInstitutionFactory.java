package net.deuce.moman.fi.model;

import net.deuce.moman.model.EntityFactory;

import org.springframework.stereotype.Component;

@Component
public class FinancialInstitutionFactory extends EntityFactory<FinancialInstitution> {

	public FinancialInstitution buildEntity(String id, String name, String url,
			String financialInstitutionId, String organization) {
		FinancialInstitution entity = super.buildEntity(FinancialInstitution.class, id);
		entity.setName(name);
		entity.setUrl(url);
		entity.setFinancialInstitutionId(financialInstitutionId);
		entity.setOrganization(organization);
		return entity;
	}
	
	public FinancialInstitution newEntity(String name, String url,
			String financialInstitutionId, String organization) {
		return buildEntity(createUuid(), name, url,
				financialInstitutionId, organization);
	}
}
