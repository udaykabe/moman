package net.deuce.moman.entity.model.fi;

import net.deuce.moman.entity.model.EntityFactory;

public interface FinancialInstitutionFactory extends EntityFactory<FinancialInstitution> {

	public FinancialInstitution buildEntity(String id, String name, String url,
			String financialInstitutionId, String organization);
	
	public FinancialInstitution newEntity(String name, String url,
			String financialInstitutionId, String organization);
}
