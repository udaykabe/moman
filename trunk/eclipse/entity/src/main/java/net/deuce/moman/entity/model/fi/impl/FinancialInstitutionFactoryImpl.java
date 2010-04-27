package net.deuce.moman.entity.model.fi.impl;

import net.deuce.moman.entity.model.fi.FinancialInstitution;
import net.deuce.moman.entity.model.fi.FinancialInstitutionFactory;
import net.deuce.moman.entity.model.impl.EntityFactoryImpl;

import org.springframework.stereotype.Component;

@Component("financialInstitutionFactory")
public class FinancialInstitutionFactoryImpl extends EntityFactoryImpl<FinancialInstitution>
implements FinancialInstitutionFactory {

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
